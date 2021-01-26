import re
from functools import reduce

import matplotlib
import pandas as pd
import numpy as np
import xlsxwriter
import xlrd
import matplotlib.pyplot as plt
import glob
import csv

from PythonProject.iteration1.main.Observable.Observer import Observable
from PythonProject.iteration1.main.builder.AnswerKeyBuilder import AnswerKeyBuilder
from PythonProject.iteration1.main.builder.PollBuilder import PollBuilder
from PythonProject.iteration1.main.builder.StudentsBuilder import StudentBuilder
from PythonProject.iteration1.main.models.Poll import Poll, SubPoll
from PythonProject.iteration1.main.repositories.abstarct.AbstarctStudentRepository import AbstractRepository
from PythonProject.iteration1.main.repositories.StudentRepositoryImpl import StudentRepository
import os


class Program:
    student_list_path = r"assets/student_list"
    polls_path = r"assets/polls"
    answer_keys = r"assets/answer_keys"
    PATTERN = re.compile(r'''((?:[^,"']|"[^"]*"|'[^']*')+)''')
    def __init__(self):
        self.totalAttandanceQuizes = 0
        self.quizListener = Observable()
        self.quizListener.subscribe(self.addQuiz)
        self.polls = []
        self.entrance()

    def addQuiz(self, temp):
        self.totalAttandanceQuizes = self.totalAttandanceQuizes + 1

    def entrance(self):
        print("Hello")
        pollsDataFrames = [file for file in glob.glob(os.path.join(self.polls_path, "*.csv"))]
        studentDf = [pd.read_excel(file, engine='openpyxl') for file in glob.glob(os.path.join(self.student_list_path, "*.xlsx"))][
            0]
        pollsData = {}

        studentsBuilder = StudentBuilder(studentDf)
        self.studentsRepository = StudentRepository(studentsBuilder.student_list)
        self.studentsRepository.numberPairStudentRepo = self.studentsRepository.createRepoByUniqueID(
            studentsBuilder.student_list,
            "number")
        self.pollParsing(pollsData,pollsDataFrames)
        self.answerKeyParse()
        self.attendanceParser()

    def pollParsing(self, pollsData, pollsDataFrames):
        for pollPath in pollsDataFrames:
            with open(pollPath,"r",encoding="utf-8") as temp_f:
                poll = []
                for l in csv.reader(temp_f, quotechar='"', delimiter=',',
                                    quoting=csv.QUOTE_ALL, skipinitialspace=True):
                    poll.append(l)
                pollsData[pollPath]= pd.DataFrame(poll)
        self.pollContainer = []
        for poll in pollsData.items():
            pollBuilder = PollBuilder(poll[0], poll[1], self.studentsRepository)
            pollObj = pollBuilder.build(self.quizListener)
            self.pollContainer.append(pollObj)
        self.allPolls = []
        for pollGroup in self.pollContainer:
            for type, listOfPolls in pollGroup.polls.items():
                for subpoll in listOfPolls:
                    tempPoll = SubPoll(date=pollGroup.date, generationTime=pollGroup.generationTime,
                                       missingStudents=pollGroup.
                                       missingStudents, session=pollGroup.session, polls=subpoll,
                                       students=pollGroup.students, path=pollGroup.path)
                    tempPoll.type = type
                    self.allPolls.append(tempPoll)

    def answerKeyParse(self):
        paths = glob.glob(os.path.join(self.answer_keys, "*.txt"))
        answer_keys = []
        for path in paths:
            with open(path,"r",encoding="utf-8") as textFile:
                lines = textFile.readlines()
                container = []
                for line in lines[2:]:
                    if(line == "\n"):
                        continue
                    print(line.replace("\n",""))
                    container.append(line.replace("\n",""))
                answer_keys.append(container)
        print(answer_keys)
        answerKeyBuilder = AnswerKeyBuilder()
        answerKeyBuilder.build(answer_keys,self.allPolls)
        print("")
    def attendanceParser(self):
        pass
        # for
# def attandanceReport(self):
#     sheet = []
#     sheet.append(["ID", "NAME", "NUMBER OF CLASSES", "ATTENDED", "PERCANTAGE"])
#     workbook = xlsxwriter.Workbook('AttadanceReport.xlsx')
#     worksheet = workbook.add_worksheet()
#     for student in self.studentsRepository.studentRawRepo:
#         row = []
#         for poll, questions in student.questionsStudentAnswered.items():
#             if ("Attandance" in questions.keys()):
#                 attandanceQuizes = questions["Attandance"]
#                 student.attancePercent += 1
#                 continue
#         student.calculateAttandance(self.totalAttandanceQuizes)
#         row.append(student.number)
#         row.append(student.name + " " + student.surname)
#         row.append(self.totalAttandanceQuizes)
#         row.append(student.attancePercent)
#         row.append(student.realAttadancePercent)
#         sheet.append(row)
#     rowId = 0
#     colId = 0
#     for row in sheet:
#         x, y, z, q, k = row
#         worksheet.write(rowId, colId, x)
#         worksheet.write(rowId, colId + 1, y)
#         worksheet.write(rowId, colId + 2, z)
#         worksheet.write(rowId, colId + 3, q)
#         worksheet.write(rowId, colId + 4, k)
#         rowId += 1
#     workbook.close()
#
# def sevenPartAPollReport(self):
#     conversion = {True: 1, False: 0, None: 1}
#     name = """Poll-{name}.xlsx"""
#     tempindex = 1
#     for poll in self.polls:
#         newName = name.format(name=tempindex)
#         sheet = []
#         header = ["Student"]
#         poll.questions.sort(key=lambda x: x, reverse=True)
#         header.extend(poll.questions)
#         metrics = ["number of questions", "success percantage"]
#         header.extend(metrics)
#         sheet.append(header)
#         workbook = xlsxwriter.Workbook(name)
#         worksheet = workbook.add_worksheet()
#         for student in self.studentsRepository.studentRawRepo:
#             row = []
#             quizQuestions = poll.quizQuesitonsPair
#             attandanceQuestions = poll.attandanceQuestionsPair
#             row.append(student.number)
#             if (quizQuestions is not None):
#                 if (quizQuestions.get(student) is not None):
#                     quizQuestions.get(student).sort(key=lambda x: x.question, reverse=True)
#                     results = [conversion.get(result.result) for result in quizQuestions.get(student)]
#                     if (len(results) != len(poll.questions) - 1):
#                         for missed in range(len(results), len(poll.questions) - 1):
#                             results.append(0)
#                     row.extend(results)
#                 else:
#                     if ('Are you attending this lecture?' in poll.questions):
#                         length = len(poll.questions) - 1
#                     else:
#                         length = len(poll.questions)
#                     for index in range(length):
#                         row.append(0)
#             if (attandanceQuestions is not None):
#                 if (attandanceQuestions.get(student) is not None):
#                     row.append(1)
#                 else:
#                     row.append(0)
#             row.append(len(row) - 1)
#             try:
#                 student.pollResults[poll] = sum(row[1:len(row) - 1]) / (len(row[1:len(row) - 1]))
#                 row.append(sum(row[1:len(row) - 1]) / (len(row[1:len(row) - 1])))
#
#             except:
#                 print(row)
#                 return
#             sheet.append(row)
#         print(header[1])
#         if header[1] == "Are you attending this lecture?":
#             tempList = []
#             for i in sheet[1:]:
#                 tempList.append(i[1:-1])
#
#             lengthTempList = len(tempList[0])
#             total = np.zeros(lengthTempList)
#             for i in range(lengthTempList):
#                 for j in range(len(tempList)):
#                     total[i] += tempList[j][i]
#                 print("{}.total: ".format(i + 1), total[i])
#         else:
#             tempList = []
#             for i in sheet[1:]:
#                 tempList.append(i[1:-2])
#             lengthTempList = len(tempList[0])
#             total = np.zeros(lengthTempList)
#             for i in range(lengthTempList):
#                 for j in range(len(tempList)):
#                     total[i] += tempList[j][i]
#                 print("{}.total: ".format(i + 1), total[i])
#
#         print(total, total[1], type(total),
#               type(total[1]))  # TODO: Convert this total numpy.ndarray list with integers
#         # TODO continue: https://xlsxwriter.readthedocs.io/example_chart_column.html Charts are here easy to implement
#         df = pd.DataFrame(sheet)
#         df.to_excel(excel_writer=newName)
#         tempindex += 1
#
# def part_8(self):
#     file_name = """part_8.xlsx"""
#     workbook = xlsxwriter.Workbook(file_name)
#     worksheet = workbook.add_worksheet()
#     worksheet.write(0, 1, "Name")
#     worksheet.write(0, 2, "Surname")
#     worksheet.write(0, 3, "Student_ID")
#     row = 1
#     tempIndex = 4
#     count = 1
#     for poll in self.polls:
#         name = """Poll-{name}""".format(name=count)
#         if(len(poll.questions) == 1 and poll.questions[0] == "Are you attending this lecture?"):
#             name = name + " (Only Attandance Poll)"
#         worksheet.write(0, tempIndex, name)
#         count += 1
#         tempIndex += 1
#     for i in range(len(self.studentsRepository.studentRawRepo) - 1):
#         print(i)
#         worksheet.write(row, 0, i + 1)
#         name = self.studentsRepository.studentRawRepo[i].name
#         worksheet.write(row, 1, name)
#         surname = self.studentsRepository.studentRawRepo[i].surname
#         worksheet.write(row, 2, surname)
#         id = self.studentsRepository.studentRawRepo[i].number
#         worksheet.write(row, 3, id)
#         lastCol = 4
#         for poll,result in self.studentsRepository.studentRawRepo[i].pollResults.items():
#             worksheet.write(row, lastCol, result)
#             lastCol += 1
#         row += 1
#     workbook.close()
#
# def sevenPartB(self):
#     index = 1
#
#     for poll in self.polls:
#         storage = {}
#         pollName = f"""Poll-{index}"""
#         print(poll)
#         for question in poll.questions:
#             if(storage.get(question) is not None):
#                 continue
#             else:
#                 storage[question] = {}
#         allQuestions = []
#         # {xQ:{answer1:50,answer2:70}}
#         if(poll.attandanceQuestions is not None):
#             allQuestions.extend(poll.attandanceQuestions)
#         if(poll.quizQuesitons is not None):
#             allQuestions.extend(poll.quizQuesitons)
#         for question in allQuestions:
#             if(storage[question.question].get(question.answer) is not None):
#                 storage[question.question][question.answer] = storage[question.question][question.answer] + 1
#             else:
#                 storage[question.question][question.answer] = 0
#         qIndex = 1
#         # matplotlib.rcParams['font.sans-serif'] = ['Source Han Sans TW', 'sans-serif']
#         hfont = {'fontname': 'Helvetica'}
#         for question,answers in storage.items():
#             colorMap = []
#             for answer in answers.keys():
#                 try:
#                     if(answer == poll.answerKey[question]):
#                         colorMap.append("blue")
#                     else:
#                         colorMap.append("red")
#                 except:
#                     colorMap.append("red")
#
#
#
#             qName = f"""Q-{qIndex}.png"""
#             totalName = "assets/" + pollName + " " + qName
#             fig=plt.bar(answers.keys(), answers.values(), color=colorMap)
#             plt.xticks(rotation=90,)
#             plt.autoscale(enable=True)
#             plt.savefig(totalName, dpi=300,bbox_inches='tight')
#             qIndex += 1
#             plt.clf()
#
#         index += 1
