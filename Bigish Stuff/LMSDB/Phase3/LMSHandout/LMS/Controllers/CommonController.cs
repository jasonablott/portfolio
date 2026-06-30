using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Build.Experimental.ProjectCache;
using Microsoft.CodeAnalysis.CSharp.Syntax;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class CommonController : Controller
    {
        private readonly LMSContext db;

        public CommonController(LMSContext _db)
        {
            db = _db;
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Retreive a JSON array of all departments from the database.
        /// Each object in the array should have a field called "name" and "subject",
        /// where "name" is the department name and "subject" is the subject abbreviation.
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetDepartments()
        {
            try
            {
                // get all departments
                var query =

                    from d in db.Departments
                    orderby d.Subject
                    
                    select new
                    {
                        name = d.Name,
                        subject = d.Subject
                    };

                return Json(query.ToList());
            }
            catch
            {
                return Json(null);
            }    
            
        }



        /// <summary>
        /// Returns a JSON array representing the course catalog.
        /// Each object in the array should have the following fields:
        /// "subject": The subject abbreviation, (e.g. "CS")
        /// "dname": The department name, as in "Computer Science"
        /// "courses": An array of JSON objects representing the courses in the department.
        ///            Each field in this inner-array should have the following fields:
        ///            "number": The course number (e.g. 5530)
        ///            "cname": The course name (e.g. "Database Systems")
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetCatalog()
        {

            // alread have query for departments ^
            // get all departments
            var catalog = db.Departments.Select(d => new
                    {
                        subject = d.Subject,
                        dname = d.Name,
                        courses = d.Courses.Select(c => new
                        {
                            number = c.Number,
                            cname = c.Name
                        }).ToList()
                    }).ToList();
                
            return Json(catalog);
        }

        /// <summary>
        /// Returns a JSON array of all class offerings of a specific course.
        /// Each object in the array should have the following fields:
        /// "season": the season part of the semester, such as "Fall"
        /// "year": the year part of the semester
        /// "location": the location of the class
        /// "start": the start time in format "hh:mm:ss"
        /// "end": the end time in format "hh:mm:ss"
        /// "fname": the first name of the professor
        /// "lname": the last name of the professor
        /// </summary>
        /// <param name="subject">The subject abbreviation, as in "CS"</param>
        /// <param name="number">The course number, as in 5530</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetClassOfferings(string subject, int number)
        {

            // find course based on provided subject and number
            var course = db.Courses
                .Join(db.Departments,c => c.Department,d => d.Subject,(c, d) => new { Course = c, Department = d })
                .Where(cd => cd.Course.Number == number && cd.Department.Subject == subject)
                .Select(cd => cd.Course).FirstOrDefault();

                if (course == null) // no course found, return null
                {
                    return Json(null);
                }

            // find all the classes offered for selected course
            var offerings = db.Classes
                .Join(db.Professors,cls => cls.TaughtBy,prof => prof.UId,(cls, prof) => new { Class = cls, Professor = prof })
                .Where(cp => cp.Class.Listing == course.CatalogId)
                .Select(cp => new
                {
                    // populate list
                    season = cp.Class.Season,
                    year = cp.Class.Year,
                    location = cp.Class.Location,

                    // have to convert the start time to non-military
                    start = cp.Class.StartTime.ToString(@"hh\:mm\:ss"),
                    end = cp.Class.EndTime.ToString(@"hh\:mm\:ss"),
                    
                    fname = cp.Professor.FName,
                    lname = cp.Professor.LName
                }).ToList();

            return Json(offerings);
        }

        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <returns>The assignment contents</returns>
        public IActionResult GetAssignmentContents(string subject, int num, string season, int year, string category, string asgname)
        {            
            var contents = (from a in db.Assignments
                    join ac in db.AssignmentCategories on a.Category equals ac.CategoryId
                    join c in db.Classes on ac.InClass equals c.ClassId
                    join course in db.Courses on c.Listing equals course.CatalogId
                    where course.Department == subject
                          && course.Number == num
                          && c.Season == season
                          && c.Year == year
                          && ac.Name == category
                          && a.Name == asgname
                    select a.Contents).FirstOrDefault();

            if (contents == null)
            {
                return Content("");
            }

            return Content(contents);
        }


        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment submission.
        /// Returns the empty string ("") if there is no submission.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <param name="uid">The uid of the student who submitted it</param>
        /// <returns>The submission text</returns>
        public IActionResult GetSubmissionText(string subject, int num, string season, int year, string category, string asgname, string uid)
        {            
            var submissionText = (from s in db.Submissions
                    join a in db.Assignments on s.Assignment equals a.AssignmentId
                    join ac in db.AssignmentCategories on a.Category equals ac.CategoryId
                    join c in db.Classes on ac.InClass equals c.ClassId
                    join course in db.Courses on c.Listing equals course.CatalogId
                    where course.Department == subject
                        && course.Number == num
                        && c.Season == season
                        && c.Year == year
                        && ac.Name == category
                        && a.Name == asgname
                        && s.Student == uid
                    select s.SubmissionContents).FirstOrDefault();

            if (submissionText == null)
            {
                return Content("");
            }

            return Content(submissionText);
        }


        /// <summary>
        /// Gets information about a user as a single JSON object.
        /// The object should have the following fields:
        /// "fname": the user's first name
        /// "lname": the user's last name
        /// "uid": the user's uid
        /// "department": (professors and students only) the name (such as "Computer Science") of the department for the user. 
        ///               If the user is a Professor, this is the department they work in.
        ///               If the user is a Student, this is the department they major in.    
        ///               If the user is an Administrator, this field is not present in the returned JSON
        /// </summary>
        /// <param name="uid">The ID of the user</param>
        /// <returns>
        /// The user JSON object 
        /// or an object containing {success: false} if the user doesn't exist
        /// </returns>
        public IActionResult GetUser(string uid)
        {

            Debug.WriteLine($"\nProvided uID {uid} ----------------\n");
            // check student 
            var student = (from s in db.Students
                           where s.UId == uid
                           select s.UId).FirstOrDefault(); // will be null if not present

            if (student != null)
            {
                Debug.WriteLine($"\nStudent selected ----------------\n");
                var selected = db.Students
                    .Where(s => s.UId == uid)
                    .Select(s => new
                    {
                        fname = s.FName,
                        lName = s.LName,
                        uid = s.UId,
                        department = s.Major
                    }).ToList();

                foreach (var s in selected)
                {
                    Debug.WriteLine($"\nStudent: {s.fname} {s.lName}, UID: {s.uid}, Major: {s.department}\n");
                }

                return Json(selected);
            }

            // check admin
            var admin = (from a in db.Administrators
                           where a.UId == uid
                           select a.UId).FirstOrDefault(); // will be null if not present

            if (admin != null)
            {
                var selected = db.Administrators
                    .Where(a => a.UId == uid)
                    .Select(a => new
                    {
                        fname = a.FName,
                        lName = a.LName,
                        uid = a.UId
                    }).ToList();

                return Json(selected);
            }

            // check prof
            var prof = (from p in db.Professors
                           where p.UId == uid
                           select p.UId).FirstOrDefault(); // will be null if not present

            if (prof != null)
            {
                var selected = db.Professors
                    .Where(p => p.UId == uid)
                    .Select(p => new
                    {
                        fname = p.FName,
                        lName = p.LName,
                        uid = p.UId,
                        depatment = p.WorksIn
                    }).ToList();

                return Json(selected);
            }


            return Json(new { success = false });
        }


        /*******End code to modify********/
    }
}

