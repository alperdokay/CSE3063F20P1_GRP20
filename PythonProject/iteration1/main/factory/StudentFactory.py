from PythonProject.iteration1.main.models.student import Student


class StudentFactory:

    def createStudent(self, id, number, name, surname, ) -> Student:
        fac = Student(id,number,name,surname)
        print("Factory Created", fac)
        return fac
