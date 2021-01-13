from pandas import ExcelFile
from numpy import *

from PythonProject.iteration1.main.factory.Factory import Factory
from PythonProject.iteration1.main.models.Student import Student


class StudentBuilder:
    def __init__(self, excel: ExcelFile):
        self.student_list = []
        for value in excel.values:
            tempStudentList = [value[1],value[2],value[4],value[7]]
            if(nan in tempStudentList or type(value[1]) == str):
                continue
            student = self.buildStudents(tempStudentList)
            self.student_list.append(student)

    def buildStudents(self, parameter_list: list) -> Student:
        studentFactory = Factory()
        return studentFactory.create(Student,(parameter_list[0],parameter_list[1],parameter_list[2],parameter_list[3]))


