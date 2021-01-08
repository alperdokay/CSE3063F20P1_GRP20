import pandas as pd

from PythonProject.iteration1.main.builder.students_builder import StudentBuilder
from PythonProject.iteration1.main.repositories.abstarct.abstract_student_repository import AbstactStudentFactory
from PythonProject.iteration1.main.repositories.student_repository_impl import StudentRepository


class Program:
    student_list_path = r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CES3063_Fall2020_rptSinifListesi.XLS.xlsx"
    poll_list_path = [
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport.csv - "
        r"CSE3063_20201124_Tue_zoom_PollReport.csv.csv",
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport.csv - "
        r"CSE3063_20201123_Mon_zoom_PollReport.csv (1).csv"]

    def __init__(self):
        self.entrance()
        pass

    def entrance(self):
        print("Welcome to the Poll analysis application")
        studentsDataFrame = pd.read_excel(self.student_list_path, engine='openpyxl')
        studentsBuilder = StudentBuilder(studentsDataFrame)
        studentsRepository = StudentRepository(studentsBuilder.student_list)
        studentsRepository.numberPairStudentRepo = studentsRepository.createRepoByUniqueID(studentsBuilder.student_list, "number")
        pollDataFrames = []
        for path in self.poll_list_path:
            pollDataFrame = pd.read_csv(path)
            pollDataFrames.append(pollDataFrame)


