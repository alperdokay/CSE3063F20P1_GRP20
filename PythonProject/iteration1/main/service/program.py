from functools import reduce

import pandas as pd
import numpy as np
import xlsxwriter
import xlrd

from PythonProject.iteration1.main.Observable.Observer import Observable
from PythonProject.iteration1.main.builder.AnswerKeyBuilder import AnswerKeyBuilder
from PythonProject.iteration1.main.builder.PollBuilder import PollBuilder
from PythonProject.iteration1.main.builder.StudentsBuilder import StudentBuilder
from PythonProject.iteration1.main.repositories.abstarct.AbstarctStudentRepository import AbstractRepository
from PythonProject.iteration1.main.repositories.StudentRepositoryImpl import StudentRepository
from itertools import chain


class Program:
    student_list_path = r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CES3063_Fall2020_rptSinifListesi.XLS.xlsx"
    poll_list_path = [
        r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport.csv - CSE3063_20201123_Mon_zoom_PollReport.csv (1).csv"
        ,
        r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport.csv - CSE3063_20201124_Tue_zoom_PollReport.csv.csv",
    ]
    answer_keys = [
        r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport_AnswerKey (1).csv"
        ,
        r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport_AnswerKey.csv"
    ]

    def __init__(self):
        self.totalAttandanceQuizes = 0
        self.quizListener = Observable()
        self.quizListener.subscribe(self.addQuiz)
        self.polls = []
        self.entrance()

        pass

    def addQuiz(self, temp):
        self.totalAttandanceQuizes = self.totalAttandanceQuizes + 1

    def entrance(self):
        studentsDataFrame = pd.read_excel(self.student_list_path, engine='openpyxl')
        studentsBuilder = StudentBuilder(studentsDataFrame)
        self.studentsRepository = StudentRepository(studentsBuilder.student_list)
        self.studentsRepository.numberPairStudentRepo = self.studentsRepository.createRepoByUniqueID(
            studentsBuilder.student_list,
            "number")
        pollDataFrames = {}
        answerKeyDataFrames = {}
        for path in self.poll_list_path:
            pollDataFrame = pd.read_csv(path)
            pollDataFrames[path] = pollDataFrame
        for pollFrame in pollDataFrames.items():
            pollBuilder = PollBuilder(pollFrame[0], pollFrame[1], self.studentsRepository)
            pollObj = pollBuilder.build(self.quizListener)
            self.polls.append(pollObj)
        for answerKeyPath in self.answer_keys:
            answerKeyDataFrame = pd.read_csv(answerKeyPath)
            answerKeyDataFrames[answerKeyPath] = answerKeyDataFrame
            answerKeyBuilder = AnswerKeyBuilder()
            answerKeyBuilder.build(answerKeyDataFrame, self.polls)
        self.attandanceReport()
        # self.sevenPartAPollReport()
        self.part_8()

    def attandanceReport(self):
        sheet = []
        sheet.append(["ID", "NAME", "NUMBER OF CLASSES", "ATTENDED", "PERCANTAGE"])
        workbook = xlsxwriter.Workbook('AttadanceReport.xlsx')
        worksheet = workbook.add_worksheet()
        for student in self.studentsRepository.studentRawRepo:
            row = []
            for poll, questions in student.questionsStudentAnswered.items():
                if ("Attandance" in questions.keys()):
                    attandanceQuizes = questions["Attandance"]
                    student.attancePercent += 1
                    continue
            student.calculateAttandance(self.totalAttandanceQuizes)
            row.append(student.number)
            row.append(student.name + " " + student.surname)
            row.append(self.totalAttandanceQuizes)
            row.append(student.attancePercent)
            row.append(student.realAttadancePercent)
            sheet.append(row)
        rowId = 0
        colId = 0
        for row in sheet:
            x, y, z, q, k = row
            worksheet.write(rowId, colId, x)
            worksheet.write(rowId, colId + 1, y)
            worksheet.write(rowId, colId + 2, z)
            worksheet.write(rowId, colId + 3, q)
            worksheet.write(rowId, colId + 4, k)
            rowId += 1
        workbook.close()

    def sevenPartAPollReport(self):
        conversion = {True: 1, False: 0, None: 1}
        name = """Poll-{name}.xlsx"""
        tempindex = 1
        for poll in self.polls:
            newName = name.format(name=tempindex)
            sheet = []
            header = ["Student"]
            poll.questions.sort(key=lambda x: x, reverse=True)
            header.extend(poll.questions)
            metrics = ["number of questions", "success percantage"]
            header.extend(metrics)
            sheet.append(header)
            workbook = xlsxwriter.Workbook(name)
            worksheet = workbook.add_worksheet()
            for student in self.studentsRepository.studentRawRepo:
                row = []
                quizQuestions = poll.quizQuesitonsPair
                attandanceQuestions = poll.attandanceQuestionsPair
                row.append(student.number)
                if (quizQuestions is not None):
                    if (quizQuestions.get(student) is not None):
                        quizQuestions.get(student).sort(key=lambda x: x.question, reverse=True)
                        results = [conversion.get(result.result) for result in quizQuestions.get(student)]
                        if (len(results) != len(poll.questions) - 1):
                            for missed in range(len(results), len(poll.questions) - 1):
                                results.append(0)
                        row.extend(results)
                    else:
                        if ('Are you attending this lecture?' in poll.questions):
                            length = len(poll.questions) - 1
                        else:
                            length = len(poll.questions)
                        for index in range(length):
                            row.append(0)
                if (attandanceQuestions is not None):
                    if (attandanceQuestions.get(student) is not None):
                        row.append(1)
                    else:
                        row.append(0)
                row.append(len(row) - 1)
                try:
                    student.pollResults[poll] = sum(row[1:len(row) - 1]) / (len(row[1:len(row) - 1]))
                    row.append(sum(row[1:len(row) - 1]) / (len(row[1:len(row) - 1])))

                except:
                    print(row)
                    return
                sheet.append(row)
            print(header[1])
            if header[1] == "Are you attending this lecture?":
                tempList = []
                for i in sheet[1:]:
                    tempList.append(i[1:-1])

                lengthTempList = len(tempList[0])
                total = np.zeros(lengthTempList)
                for i in range(lengthTempList):
                    for j in range(len(tempList)):
                        total[i] += tempList[j][i]
                    print("{}.total: ".format(i + 1), total[i])
            else:
                tempList = []
                for i in sheet[1:]:
                    tempList.append(i[1:-2])
                lengthTempList = len(tempList[0])
                total = np.zeros(lengthTempList)
                for i in range(lengthTempList):
                    for j in range(len(tempList)):
                        total[i] += tempList[j][i]
                    print("{}.total: ".format(i + 1), total[i])

            print(total, total[1], type(total),
                  type(total[1]))  # TODO: Convert this total numpy.ndarray list with integers
            # TODO continue: https://xlsxwriter.readthedocs.io/example_chart_column.html Charts are here easy to implement
            df = pd.DataFrame(sheet)
            df.to_excel(excel_writer=newName)
            tempindex += 1

    def part_8(self):
        file_name = """part_8.xlsx"""
        workbook = xlsxwriter.Workbook(file_name)
        worksheet = workbook.add_worksheet()
        worksheet.write(0, 1, "Name")
        worksheet.write(0, 2, "Surname")
        worksheet.write(0, 3, "Student_ID")
        row = 1
        for i in range(len(self.studentsRepository.studentRawRepo) - 1):
            worksheet.write(row, 0, i + 1)
            name = self.studentsRepository.studentRawRepo[i].name
            worksheet.write(row, 1, name)
            surname = self.studentsRepository.studentRawRepo[i].surname
            worksheet.write(row, 2, surname)
            id = self.studentsRepository.studentRawRepo[i].number
            worksheet.write(row, 3, id)
            loc_quiz_poll = r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\main\Poll-1.xlsx"
            excel_quiz = pd.read_excel(loc_quiz_poll, engine='openpyxl')
            for y in range(1, len(excel_quiz)):
                if int(excel_quiz.iloc[y][0]) == int(id):
                    worksheet.write(row, 4, excel_quiz.iloc[y][13])
                    worksheet.write(0, 4, "Success rate for quiz poll")
            quiz_poll_DataFrame = pd.read_csv(
                r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport.csv - CSE3063_20201123_Mon_zoom_PollReport.csv (1).csv")
            for k in range(1, len(quiz_poll_DataFrame)):
                date = quiz_poll_DataFrame["Submitted Date/Time"]
                date_added = (date[0][:12])
                worksheet.write(0, 5, "Quiz_Poll_Date")
                worksheet.write(row, 5, date_added)
            loc_attendance_poll = r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\main\Poll-2.xlsx"
            excel_attendance = pd.read_excel(loc_attendance_poll, engine='openpyxl')
            for y in range(1, len(excel_attendance)):
                if int(excel_attendance.iloc[y][0]) == int(id):
                    worksheet.write(row, 6, excel_attendance.iloc[y][3])
                    worksheet.write(0, 6, "Success rate for attendance poll")
            attendance_poll_DataFrame = pd.read_csv(
                r"C:\Users\dialajubeh\Documents\CSE3063F20P1_GRP20\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport.csv - CSE3063_20201124_Tue_zoom_PollReport.csv.csv")
            for k in range(1, len(attendance_poll_DataFrame)):
                date_atttendance = attendance_poll_DataFrame["Submitted Date/Time"]
                date_added_atttendance = (date_atttendance[0][:12])
                worksheet.write(0, 7, "Attendance_Poll_Date")
                worksheet.write(row, 7, date_added_atttendance)
            for m in range(1, len(excel_quiz)):
                if int(excel_quiz.iloc[m][0]) == int(id):
                    performance_by_id = ((excel_quiz.iloc[m][13] + excel_attendance.iloc[m][3]) / 2) * 100
                    performance_by_id = "{:.2f}".format(performance_by_id)
                    performance_by_id = str(performance_by_id) + "%"
                    worksheet.write(row, 8, performance_by_id)
                    worksheet.write(0, 8, "Performance per week")
        row += 1
        workbook.close()
