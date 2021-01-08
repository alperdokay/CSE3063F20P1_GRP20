from PythonProject.iteration1.main.repositories.abstarct.abstract_student_repository import AbstactStudentFactory

class StudentRepository(AbstactStudentFactory):
    def __init__(self):
        self.studentRepository = {}

    def createRepoByUniqueID(self,setofElements,nameOfUniqueElement):
        for i in setofElements:
            if(getattr(i,nameOfUniqueElement) in self.studentRepository):
                continue
            else:
                self.studentRepository[i.getNumber()] = i
        print(self.studentRepository)

