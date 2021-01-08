import pandas as pd

from PythonProject.iteration1.main.builder.students_builder import StudentBuilder
from PythonProject.iteration1.main.repositories.abstarct.abstract_student_repository import AbstactStudentFactory
from PythonProject.iteration1.main.repositories.student_repository_impl import StudentRepository

class Program:
    student_list_path = r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\main\CES3063_Fall2020_rptSinifListesi.XLS.xlsx"

    def __init__(self):
        self.entrance()
        pass

    def entrance(self):
        print("Welcome to the Poll analysis application")
        studentsDataFrame = pd.read_excel(self.student_list_path, engine='openpyxl')
        studentsBuilder = StudentBuilder(studentsDataFrame)
        studentsRepository = StudentRepository()
        studentsRepository.createRepoByUniqueID(studentsBuilder.student_list,"number")




