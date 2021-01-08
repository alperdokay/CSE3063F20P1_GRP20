from pandas import ExcelFile
from numpy import *

from PythonProject.iteration1.main.factory.StudentFactory import StudentFactory
from PythonProject.iteration1.main.models.student import Student


class StudentBuilder:

    def __init__(self, excel: ExcelFile):
        self.student_list = [];
        for value in excel.values:
            tempStudentList = [value[1],value[2],value[4],value[7]]
            if(nan in tempStudentList or type(value[1]) == str):
                continue
            student = self.__build_student(tempStudentList)
            self.student_list.append(student)

    def __build_student(self , parameter_list: list) -> Student:
        studentFactory = StudentFactory()
        return studentFactory.createStudent(parameter_list[0],parameter_list[1],parameter_list[2],parameter_list[3])

