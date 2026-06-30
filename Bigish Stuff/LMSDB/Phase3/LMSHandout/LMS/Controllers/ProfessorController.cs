using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data.Common;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OutputCaching;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS_CustomIdentity.Controllers
{
    [Authorize(Roles = "Professor")]
    public class ProfessorController : Controller
    {

        private readonly LMSContext db;

        public ProfessorController(LMSContext _db)
        {
            db = _db;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Students(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult Class(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult Categories(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult CatAssignments(string subject, string num, string season, string year, string cat)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            return View();
        }

        public IActionResult Assignment(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Submissions(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Grade(string subject, string num, string season, string year, string cat, string aname, string uid)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            ViewData["uid"] = uid;
            return View();
        }

        /*******Begin code to modify********/


        /// <summary>
        /// Returns a JSON array of all the students in a class.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "dob" - date of birth
        /// "grade" - the student's grade in this class
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetStudentsInClass(string subject, int num, string season, int year) // DONE
        {
            if (string.IsNullOrWhiteSpace(subject) || string.IsNullOrWhiteSpace(season))
            {
                return Json(new { success = false, error = "Subject and Semester required." });
            }
            try
            {
                // find class to get classID
                var classID = (
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId).FirstOrDefault();
                // if no result, class was not found
                if (classID == 0)
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                // use classID to query enrolled table
                var studentsInClass =
                    from e in db.Enrolleds
                    join s in db.Students on e.Student equals s.UId
                    where e.Class == classID
                    orderby s.LName
                    select new
                    {
                        fname = s.FName,
                        lname = s.LName,
                        uid = s.UId,
                        dob = s.Dob,
                        grade = e.Grade
                    };
                return Json(studentsInClass.ToList());
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.GetStudentsInClass() method" });
            }
        }



        /// <summary>
        /// Returns a JSON array with all the assignments in an assignment category for a class.
        /// If the "category" parameter is null, return all assignments in the class.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The assignment category name.
        /// "due" - The due DateTime
        /// "submissions" - The number of submissions to the assignment
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class, 
        /// or null to return assignments from all categories</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInCategory(string subject, int num, string season, int year, string category)
        {
            if (string.IsNullOrWhiteSpace(subject) || string.IsNullOrWhiteSpace(season))
            {
                return Json(new { success = false, error = "Subject and Semester required." });
            }
            try
            {
                // Find classID from Classes, Courses   
                var classID = (
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId).FirstOrDefault();
                // if no result, class was not found
                if (classID == 0)
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                var assignmentsInClass =
                    from assign in db.Assignments
                    join assignCat in db.AssignmentCategories on assign.Category equals assignCat.CategoryId
                    where assignCat.InClass == classID
                        && (category == null || assignCat.Name == category)
                    select new
                    {
                        aname = assign.Name,
                        cname = assignCat.Name,
                        due = assign.Due,
                        submissions = (
                            from sub in db.Submissions
                            where sub.Assignment == assign.AssignmentId
                            select sub).Count()
                    };

                return Json(assignmentsInClass.ToList());
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.GetAssignmentsInCategory() method" });
            }
        }


        /// <summary>
        /// Returns a JSON array of the assignment categories for a certain class.
        /// Each object in the array should have the folling fields:
        /// "name" - The category name
        /// "weight" - The category weight
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentCategories(string subject, int num, string season, int year)
        {
            try
            {
                // Find classID from Classes, Courses   
                var query1 =
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId;
                // if no result, class was not found
                if (!query1.Any())
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                var classID = query1.FirstOrDefault();
                // get name and weight from AssignmentCategories
                // where AssignmentCategories.InClass == Classes.ClassID 
                var query2 =
                    from ac in db.AssignmentCategories
                    where ac.InClass == classID
                    select new
                    {
                        name = ac.Name,
                        weight = ac.Weight
                    };
                return Json(query2.ToList());
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred" });
            }
        }

        /// <summary>
        /// Creates a new assignment category for the specified class.
        /// If a category of the given class with the given name already exists, return success = false.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The new category name</param>
        /// <param name="catweight">The new category weight</param>
        /// <returns>A JSON object containing {success = true/false} </returns>
        public IActionResult CreateAssignmentCategory(string subject, int num, string season, int year, string category, int catweight)
        {
            try
            {
                // Find classID from Classes, Courses   
                var query1 =
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId;
                // if no result, class was not found
                if (!query1.Any())
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                var classID = query1.FirstOrDefault();
                // check if category already exists
                var query2 =
                    from cat in db.AssignmentCategories
                    where cat.Name == category &&
                        cat.InClass == classID
                    select cat;
                if (query2.Any())
                {
                    return Json(new { success = false, error = "Category already exists" });
                }
                // insert category where Name = category, Weight = catweight, inClass = class from first query
                var c = new AssignmentCategory
                {
                    Name = category,
                    Weight = (uint)catweight, // assuming catweight is a positive number 
                    InClass = classID
                };
                db.AssignmentCategories.Add(c);
                db.SaveChanges();

                return Json(new { success = true });
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred" });
            }
        }

        /// <summary>
        /// Creates a new assignment for the given class and category. **See Assignment:Auto-Grading for details**
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="asgpoints">The max point value for the new assignment</param>
        /// <param name="asgdue">The due DateTime for the new assignment</param>
        /// <param name="asgcontents">The contents of the new assignment</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult CreateAssignment(string subject, int num, string season, int year, string category, string asgname, int asgpoints, DateTime asgdue, string asgcontents)
        {
            try
            {
                // get classID for this class:
                // Find classID from Classes, Courses   
                var classID = (
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId).FirstOrDefault();
                // if no result, class was not found
                if (classID == 0)
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                // get the AssignmentCategories.CategoryID using Name, ClassID
                var acatID = (
                    from ac in db.AssignmentCategories
                    where ac.Name == category &&
                        ac.InClass == classID
                    select ac.CategoryId).FirstOrDefault();
                // if no result, assignment category was not found
                if (acatID == 0)
                {
                    return Json(new { success = false, error = "Assignment category does not exist" });
                }
                // fields needed for assignment: Name = asgname, Contents = asgcontents, Due = asgdue, MaxPoints = asgpoints, 
                // Category = category
                var newAsg = new Assignment
                {
                    Name = asgname,
                    Contents = asgcontents,
                    Due = asgdue,
                    MaxPoints = (uint)asgpoints,
                    Category = acatID
                };
                db.Assignments.Add(newAsg);
                db.SaveChanges();
                // all student's in this class' grades should now be updated (if no submission, grade is 0)
                // use CalculateGrade helper to update grades of all students in this class

                // get all students in class
                var studentsInClass = (
                    from e in db.Enrolleds
                    where e.Class == classID
                select e.Student
                ).ToList();

                // update grade for each student
                foreach (var studentUID in studentsInClass)
                {
                    CalculateGrade(studentUID, classID);
                }

                return Json(new { success = true });
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.CreateAssignment()" });
            }
        }


        /// <summary>
        /// Gets a JSON array of all the submissions to a certain assignment.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "time" - DateTime of the submission
        /// "score" - The score given to the submission
        /// 
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetSubmissionsToAssignment(string subject, int num, string season, int year, string category, string asgname)
        {
            try
            {
                // Find classID from Classes, Courses for class based on provided information   
                var classID = (
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId).FirstOrDefault();
                // if no result, class was not found
                if (classID == 0)
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                // get AssignmentCategory.CategoryID where AssignmentCategory.Name = category
                var catID = (
                    from ac in db.AssignmentCategories
                    where ac.Name == category
                        && ac.InClass == classID
                    select ac.CategoryId).FirstOrDefault();
                if (catID == 0)
                {
                    return Json(new { success = false, error = "Assignment Category does not exist" });
                }
                // get AssignmentID from Assignments.AssignmentID where Assignment.Name = asgname and 
                // Assignment.Category = catID
                var aID = (
                    from a in db.Assignments
                    where a.Name == asgname
                        && a.Category == catID
                    select a.AssignmentId).FirstOrDefault();
                if (aID == 0)
                {
                    return Json(new { success = false, error = "Assignment does not exist" });
                }
                // return Students.fName, Students.lName, Students.UId, Submissions.Time, Submissions.Score 
                // from join of Students and Submissions where Submissions.Assignment = AssignmentID 
                var submissions =
                    from stu in db.Students
                    join sub in db.Submissions on stu.UId equals sub.Student
                    where sub.Assignment == aID
                    select new
                    {
                        fname = stu.FName,
                        lname = stu.LName,
                        uid = stu.UId,
                        time = sub.Time,
                        score = sub.Score
                    };
                return Json(submissions.ToList());
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.GetSubmissionsToAssignment() method" });
            }
        }


        /// <summary>
        /// Set the score of an assignment submission. **See Assignment:Auto-Grading for details**
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <param name="uid">The uid of the student who's submission is being graded</param>
        /// <param name="score">The new score for the submission</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult GradeSubmission(string subject, int num, string season, int year, string category, string asgname, string uid, int score)
        {
            try
            {
                // get to submissions table and update Score

                // get class id
                var classID = (
                    from cla in db.Classes
                    join cor in db.Courses on cla.Listing equals cor.CatalogId
                    where cla.Season == season
                        && cor.Department == subject
                        && cor.Number == num
                        && cla.Year == year
                    select cla.ClassId).FirstOrDefault();
                // if no result, class was not found
                if (classID == 0)
                {
                    return Json(new { success = false, error = "Class does not exist" });
                }
                // get AssignmentCategories.CategoryID using inClass = classID, Name = category
                var catID = (
                    from ac in db.AssignmentCategories
                    where ac.Name == category
                        && ac.InClass == classID
                    select ac.CategoryId).FirstOrDefault();
                if (catID == 0)
                {
                    return Json(new { success = false, error = "Assignment Category does not exist" });
                }
                // get Assignments.AssignmentID using CategoryID, asgname
                var aID = (
                    from a in db.Assignments
                    where a.Name == asgname
                        && a.Category == catID
                    select a.AssignmentId).FirstOrDefault();
                if (aID == 0)
                {
                    return Json(new { success = false, error = "Assignment does not exist" });
                }
                // update submission where Submissions.Student = uid, Submissions.Assignment = Assignments.AssignmentID
                // and Submissions.Score = score
                var submissionToScore = (
                    from sub in db.Submissions
                    where sub.Student == uid &&
                        sub.Assignment == aID
                    select sub).FirstOrDefault();
                if (submissionToScore == null)
                {
                    return Json(new { success = false, error = "Submission not found" });
                }
                submissionToScore.Score = (uint)score;
                db.SaveChanges();
                // Now that Submissions.Score is updated, update Enrolled.Grade for this student in this class
                CalculateGrade(uid, classID);

                return Json(new { success = true });
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.GradeSubmission() method" });
            }
        }


        /// <summary>
        /// Returns a JSON array of the classes taught by the specified professor
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester in which the class is taught
        /// "year" - The year part of the semester in which the class is taught
        /// </summary>
        /// <param name="uid">The professor's uid</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            if (string.IsNullOrWhiteSpace(uid))
            {
                return Json(new { success = false, error = "Professor UID is required." });
            }
            try
            {
                // Join Classes and Courses
                var myClasses =
                    from cl in db.Classes
                    join co in db.Courses on cl.Listing equals co.CatalogId
                    // search table where TaughtBy == uid
                    where cl.TaughtBy == uid
                    // return subject = Courses.Department, number = Courses.Number, name = Courses.Name, season = Classes.Season
                    // year = Classes.Year
                    select new
                    {
                        subject = co.Department,
                        number = co.Number,
                        name = co.Name,
                        season = cl.Season,
                        year = cl.Year
                    };
                return Json(myClasses.ToList());
            }
            catch
            {
                return Json(new { success = false, error = "An error occurred in ProfessorController.GetMyClasses() method" });
            }
        }

        /// <summary>
        /// Calculates and updates a student's grade
        /// </summary>
        /// <param name="uid">The students's uid</param>
        /// <param name="classID">The ClassID of the class to calculate the student's grade in</param>
        /// <returns>void</returns>
        public void CalculateGrade(string uid, uint classID)
        {
            // Get Enrollment for this student and class
            var enrollment = (
                from e in db.Enrolleds
                where e.Student == uid && e.Class == classID
                select e
                ).FirstOrDefault();

            // if student not in class return, nothing to do
            if (enrollment == null)
                return;

            // find assignment categories in the class
            var categories = (
                from cat in db.AssignmentCategories
                where cat.InClass == classID
                select cat
                ).ToList();

            // if no categories there is no grade to calculate yet
            if (categories.Count == 0)
            {
                enrollment.Grade = "--";
                db.SaveChanges();
                return;
            }

            // otherwise calculate grade 

            // variables to track weights for categories
            double totalWeightedScore = 0.0;
            double totalWeightsUsed = 0.0;

            // for each assignment category in the class
            foreach (var cat in categories)
            {
                // get all assignments in category
                var assignments = (
                    from a in db.Assignments
                    where a.Category == cat.CategoryId
                    select a
                    ).ToList();

                // if no assignments in category, move to next category
                if (assignments.Count == 0)
                    continue;

                // variables to track points for assignments
                double earnedPoints = 0.0;
                double maxPoints = 0.0;

                // for each assignment in this category, calculate the total score and max points
                foreach (var assignment in assignments)
                {
                    maxPoints += assignment.MaxPoints; // update max points

                    // find the student's submission
                    var submission = (
                        from s in db.Submissions
                        where s.Assignment == assignment.AssignmentId && s.Student == uid
                        select s
                        ).FirstOrDefault();

                    // update earned points with score, or 0 if submission is null
                    earnedPoints += (submission != null) ? submission.Score : 0.0;
                }

                // if no points have been calculated move to next category
                if (maxPoints == 0)
                    continue;

                // otherwise calculate the updated score
                double percentage = earnedPoints / maxPoints;
                totalWeightedScore += percentage * cat.Weight;
                totalWeightsUsed += cat.Weight;
            }

            // if no weight has been calculated, there are no assignment submissions graded yet, so there is no grade yet. 
            if (totalWeightsUsed == 0)
            {
                enrollment.Grade = "--";
            }

            // if there are weights, scale the total and use to calculate the updated grade for the student
            else
            {
                double scaleFactor = 100.0 / totalWeightsUsed;
                double finalPercentage = totalWeightedScore * scaleFactor;
                enrollment.Grade = ConvertPercentageToLetter(finalPercentage);
            }
            db.SaveChanges();
        }
    
        /// <summary>
        /// Converts percentage grade to a letter grade based on syllabus policy
        /// </summary>
        /// <param name="percent">The grade in percentage form to convert to a letter grade</param>
        /// <returns>string a 1 or 2 character representation of a grade, such as 'A' or 'B+' </returns>
        private static string ConvertPercentageToLetter(double percent)
            {
                if (percent >= 93) return "A";
                else if (percent >= 90) return "A-";
                else if (percent >= 87) return "B+";
                else if (percent >= 83) return "B";
                else if (percent >= 80) return "B-";
                else if (percent >= 77) return "C+";
                else if (percent >= 73) return "C";
                else if (percent >= 70) return "C-";
                else if (percent >= 67) return "D+";
                else if (percent >= 63) return "D";
                else if (percent >= 60) return "D-";
                else return "E";
            }

        /*******End code to modify********/
        
    }
}

