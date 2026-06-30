using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Build.Experimental.ProjectCache;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class AdministratorController : Controller
    {
        private readonly LMSContext db;

        public AdministratorController(LMSContext _db)
        {
            db = _db;
        }

        // GET: /<controller>/
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Department(string subject)
        {
            ViewData["subject"] = subject;
            return View();
        }

        public IActionResult Course(string subject, string num)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Create a department which is uniquely identified by it's subject code
        /// </summary>
        /// <param name="subject">the subject code</param>
        /// <param name="name">the full name of the department</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the department already exists, true otherwise.</returns>
        public IActionResult CreateDepartment(string subject, string name)
        {
            if (string.IsNullOrWhiteSpace(subject) || string.IsNullOrWhiteSpace(name))
            {
                return Json(new { success = false, error = "Subject and name are required." });
            }

            try
            {
                // false if department already exists
                var query =
                from dep in db.Departments
                where dep.Subject == subject
                select dep;

                if (query.Any())
                {
                    return Json(new { success = false });
                }

                // if department added successfully
                var d = new Department
                {
                    Name = name,
                    Subject = subject
                };

                db.Departments.Add(d);
                db.SaveChanges();

                return Json(new { success = true });
            }

            catch 
            {
                return Json(new { success = false, error = "An error occurred in AdministratorController CreateDepartment()" });
            } 
            
        }


        /// <summary>
        /// Returns a JSON array of all the courses in the given department.
        /// Each object in the array should have the following fields:
        /// "number" - The course number (as in 5530)
        /// "name" - The course name (as in "Database Systems")
        /// </summary>
        /// <param name="subjCode">The department subject abbreviation (as in "CS")</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetCourses(string subject)
        {
            if (string.IsNullOrWhiteSpace(subject))
            {
                return Json(new { success = false, error = "Subject is required." });
            }

            try
            {
                // get all courses with dept=subject
                var query =
                    from c in db.Courses
                    where c.Department.Equals(subject)
                    orderby c.Name
                    select new
                    {
                        number = c.Number,
                        name = c.Name
                    };

                return Json(query.ToList());
            }

            catch 
            {
                return Json(new { success = false, error = "An error occurred in AdministratorController GetCourses()" });
            } 
            
        }

        /// <summary>
        /// Returns a JSON array of all the professors working in a given department.
        /// Each object in the array should have the following fields:
        /// "lname" - The professor's last name
        /// "fname" - The professor's first name
        /// "uid" - The professor's uid
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetProfessors(string subject)
        {
            if (string.IsNullOrWhiteSpace(subject))
            {
                return Json(new { success = false, error = "Subject is required." });
            }

            try
            {
                // get all professors with dept=subject
                var query =
                    from p in db.Professors
                    where p.WorksIn.Equals(subject)
                    orderby p.LName
                    select new
                    {
                        fname = p.FName,
                        lname = p.LName,
                        uid = p.UId
                    };

                return Json(query.ToList());
            }

            catch 
            {
                return Json(new { success = false, error = "An error occurred in AdministratorController GetProfessors()" });
            } 
        }



        /// <summary>
        /// Creates a course.
        /// A course is uniquely identified by its number + the subject to which it belongs
        /// </summary>
        /// <param name="subject">The subject abbreviation for the department in which the course will be added</param>
        /// <param name="number">The course number</param>
        /// <param name="name">The course name</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the course already exists, true otherwise.</returns>
        public IActionResult CreateCourse(string subject, int number, string name)
        {
            if (string.IsNullOrWhiteSpace(subject) || string.IsNullOrWhiteSpace(name)) // could add check of number too if needed
            {
                return Json(new { success = false, error = "Subject and name are required." });
            }

            try
            {
                // false if course already exists
                var query =
                from cor in db.Courses
                where cor.Department == subject &&
                cor.Number == number

                select cor;

                if (query.Any())
                {
                    return Json(new { success = false });
                }

                // if course added successfully
                var c = new Course
                {
                    Department = subject,
                    Name = name,
                    Number = (uint)number
                };

                db.Courses.Add(c);
                db.SaveChanges();

                return Json(new { success = true });
            }

            catch 
            {
                return Json(new { success = false, error = "An error occurred in AdministratorController CreateCourse()" });
            }    
        }



        /// <summary>
        /// Creates a class offering of a given course.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="number">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="start">The start time</param>
        /// <param name="end">The end time</param>
        /// <param name="location">The location</param>
        /// <param name="instructor">The uid of the professor</param>
        /// <returns>A JSON object containing {success = true/false}. 
        /// false if another class occupies the same location during any time 
        /// within the start-end range in the same semester, or if there is already
        /// a Class offering of the same Course in the same Semester,
        /// true otherwise.</returns>
        public IActionResult CreateClass(string subject, int number, string season, int year, DateTime start, DateTime end, string location, string instructor)
        {            
            // first, check that inputs are valid. Could be more robust.
            if (string.IsNullOrWhiteSpace(subject) || string.IsNullOrWhiteSpace(season))
            {
                return Json(new { success = false, error = "Subject and season are required." });
            }
            try
            {

                // false if class in same location during same time
                var query1 =
                    from cl in db.Classes
                    where cl.Season == season &&
                        cl.Year == (uint) year &&
                        cl.Location == location &&
                        TimeOnly.FromDateTime(start) < cl.EndTime &&
                        cl.StartTime < TimeOnly.FromDateTime(end)
                select cl;

                if (query1.Any())
                {
                    return Json(new { success = false, error = "Schedule conflict occurred" });
                }

                // false if already a class offering of the same course in same semester
                var query2 =
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == number
                    select cla;

                    if (query2.Any())
                    {
                        return Json(new { success = false, error = "Class already exists" });
                    }

                // if course can be added successfully
                // first get listing (Courses.CatalogID) 
                var listingID = (
                    from co in db.Courses
                    where co.Department == subject
                        && co.Number == number
                    select co.CatalogId
                ).FirstOrDefault();
                // make sure listing exists
                if (listingID == 0)
                {
                    return Json(new { success = false, error = "Course not found" });
                }
                // then check that professor exists
                var instructorID =
                    from p in db.Professors
                    where p.UId == instructor
                    select p.UId;

                if (!instructorID.Any())
                {
                    return Json(new { success = false, error = "Instructor not found" });
                }
                // Now add new class
                var c = new Class
                {
                    Season = season,
                    Year = (uint)year,
                    Location = location,
                    StartTime = TimeOnly.FromDateTime(start),
                    EndTime = TimeOnly.FromDateTime(end),
                    Listing = listingID,
                    TaughtBy = instructor
                };

                db.Classes.Add(c);
                db.SaveChanges();

                return Json(new { success = true });
            }
            catch 
            {
                return Json(new { success = false, error = "An error occurred in AdministratorController CreateClass()" });
            }    
        }


        /*******End code to modify********/

    }
}

