from PythonProject.iteration1.main.models.Student import Student


class StudentFactory:

    def createStudent(self, id, number, name, surname, ) -> Student:
        fac = Student(id,number,name,surname)
        print("Factory Created", fac)
        return fac

