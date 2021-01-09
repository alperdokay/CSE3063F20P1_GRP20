from typing import Any


class Student:

    def __init__(self, id, number, name, surname):
        self.id = id
        self.number = number
        self.name = name
        self.surname = surname
        self.setFullName()
    def __str__(self) -> str:
        string = """Student with number {number} , name {name} , surname {surname}""".format(number=self.number,
                                                                                             name=self.name,
                                                                                             surname=self.surname)
        return string

    def setId(self, id):
        self.id = id

    def getId(self):
        return self.id

    def setNumber(self, number):
        self.number = number

    def getNumber(self):
        return self.number

    def setName(self, name):
        self.name = name

    def getId(self):
        return self.name

    def setSurname(self, surname):
        self.surname = id

    def getId(self):
        return self.surname

    def setFullName(self):
        fullName = """{name}{surname}""".format(name=self.name,surname=self.surname)
        self.fullName = fullName
