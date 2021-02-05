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
        print("Welcome to The Poll Analysis App")
        pollsDataFrames = [file for file in glob.glob(os.path.join(self.polls_path, "*.csv"))]
        studentDf = [pd.read_excel(file, engine='openpyxl') for file in glob.glob(os.path.join(self.student_list_path, "*.xlsx"))][
            0]
        pollsData = {}
        print("Started To Parse Students")
        studentsBuilder = StudentBuilder(studentDf)
        self.studentsRepository = StudentRepository(studentsBuilder.student_list)
        self.studentsRepository.numberPairStudentRepo = self.studentsRepository.createRepoByUniqueID(
            studentsBuilder.student_list,
            "number")
        print("Started To Parse Polls (Be Careful !!! It might take some time !)")
        self.pollParsing(pollsData,pollsDataFrames)
        print("Started To Parse Answer Keys (Be Careful !!! It might take some time !)")
        self.answerKeyParse()
        print("Calculating Attendances (Be Careful !!! It might take some time !)")
        self.attendanceParser()
        print("Poll Analysis Started (Be Careful !!! It might take some time !)")
        self.pollAnalysis()
        print("Poll Analysis foreach Student Started (Be Careful !!! It might take some time !)")
        self.studentAnalysis()
        print("Total Analysis  Started (Be Careful !!! It might take some time !)")
        self.totalAnalysis()
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
                    container.append(line.replace("\n",""))
                answer_keys.append(container)
        answerKeyBuilder = AnswerKeyBuilder()
        answerKeyBuilder.build(answer_keys,self.allPolls)

    def attendanceParser(self):
        for meeting in self.pollContainer:
            for student in meeting.transposedStudents:
                student.attentedSessions.append(meeting)
    def pollAnalysis(self):
        for poll in self.allPolls:
            if(poll.type == 'Attendance Polls'):
                continue

            name = poll.name.replace(" ","_") +"_" +str(poll.date).replace("-","_").replace(" ","_").replace(":","_")
            workbook = xlsxwriter.Workbook("assets/poll_analysis/"+name+".xlsx")
            worksheet = workbook.add_worksheet("Analysis")
            worksheet.write(0, 0, "student number")
            worksheet.write(0,1,"number of questions")
            worksheet.write(0,2,"number of correctly answered questions")
            worksheet.write(0,3,"number of wrongly answered questions")
            worksheet.write(0,4,"number of empty questions")
            worksheet.write(0,5,"accuracy percentage")
            row = 1
            for student in self.studentsRepository.studentRawRepo:
                correctQuestions = 0
                wrongQuestions = 0
                emptyQuestions = 0
                totalQuestions = 0
                score = 0
                if(student in poll.polls):
                    totalQuestions = len(poll.polls[student])
                    correctQuestions = len([question for question in poll.polls[student] if question.result == True])
                    wrongQuestions = len([question for question in poll.polls[student] if question.result == False])
                    emptyQuestions = len([question for question in poll.polls[student] if question.result == None])
                    score = (correctQuestions / totalQuestions) * 100
                worksheet.write(row, 0, student.number)
                worksheet.write(row, 1, totalQuestions)
                worksheet.write(row, 2, correctQuestions)
                worksheet.write(row, 3, wrongQuestions)
                worksheet.write(row, 4, emptyQuestions)
                worksheet.write(row, 5, score)
                row = row + 1
            workbook.close()

    def studentAnalysis(self):
        for student in self.studentsRepository.studentRawRepo:
            for poll,answers in student.pollResults.items():
                name = poll.name.replace(" ", "_") + "_" + str(poll.date).replace("-", "_").replace(" ", "_").replace(
                    ":", "_")+"_"+student.name+"_"+student.surname+"_"+str(student.number)
                workbook = xlsxwriter.Workbook("assets/student_analysis/" + name + ".xlsx")
                worksheet = workbook.add_worksheet("Analysis")
                for index in range(len(answers)):
                    text=answers[index].question
                    givenAnswer = answers[index].answer
                    correctAnswer = answers[index].correctResult
                    if(str(givenAnswer) == str(correctAnswer)):
                        answers[index].result = True
                    result = 1 if answers[index].result else 0
                    worksheet.write(index,0,text)
                    worksheet.write(index,1,givenAnswer)
                    worksheet.write(index,2,correctAnswer)
                    worksheet.write(index,3,result)
                workbook.close()
    def totalAnalysis(self):
        allData = []
        for index in range(len(self.studentsRepository.studentRawRepo)):
            student = self.studentsRepository.studentRawRepo[index]
            row = []
            row.append(index)
            row.append(student.number)
            row.append(student.name + " " + student.surname )
            totalCorrect = 0
            totalQuestions = 0
            for poll in self.allPolls:
                if (poll.type == 'Attendance Polls'):
                    continue
                if(poll in student.pollResults):
                    row.append(poll.name.replace(" ", "_") + "_" + str(poll.date).replace("-", "_").replace(" ", "_").replace(
                    ":", "_"))
                    sum = 0
                    totalQuestions = totalQuestions + len(poll.statistics.keys())
                    for question in student.pollResults[poll]:
                        if(question.result):
                            sum = sum + 1
                            totalCorrect = totalCorrect + 1
                    score = sum / len(poll.statistics.keys())
                    row.append(score*100)
                else:
                    totalQuestions = totalQuestions + len(poll.statistics.keys())
                    row.append(
                        poll.name.replace(" ", "_") + "_" + str(poll.date).replace("-", "_").replace(" ", "_").replace(
                            ":", "_"))
                    row.append(0)
            allScore = (totalCorrect / totalQuestions)*100
            row.append("Total Score")
            row.append(allScore)
            allData.append(row)
            workbook = xlsxwriter.Workbook("assets/"+"CSE3063_2020FALL_QuizGrading.xlsx")
            worksheet = workbook.add_worksheet("analysis")
            for rowIndex in range(len(allData)):
                row = allData[rowIndex]
                for colIndex in range(len(row)):
                    worksheet.write(rowIndex,colIndex,row[colIndex])
            workbook.close()

