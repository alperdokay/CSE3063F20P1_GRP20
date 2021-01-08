from PythonProject.iteration1.main.repositories.abstarct.abstract_student_repository import AbstactStudentFactory
from collections import OrderedDict

class StudentRepository(AbstactStudentFactory):
    def __init__(self,rawRepo):
        self.studentRawRepo = rawRepo
    def createRepoByUniqueID(self,setofElements,nameOfUniqueElement):
        tempRepo = OrderedDict()
        for i in setofElements:
            if(getattr(i,nameOfUniqueElement) in tempRepo):
                continue
            else:
                tempRepo[i.getNumber()] = i
        return tempRepo

