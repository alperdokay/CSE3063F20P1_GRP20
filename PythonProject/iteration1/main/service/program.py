import pandas as pd
import xlsxwriter

from PythonProject.iteration1.main.Observable.Observer import Observable
from PythonProject.iteration1.main.builder.AnswerKeyBuilder import AnswerKeyBuilder
from PythonProject.iteration1.main.builder.PollBuilder import PollBuilder
from PythonProject.iteration1.main.builder.StudentsBuilder import StudentBuilder
from PythonProject.iteration1.main.repositories.abstarct.AbstarctStudentRepository import AbstractRepository
from PythonProject.iteration1.main.repositories.StudentRepositoryImpl import StudentRepository
from itertools import chain


class Program:
    student_list_path = r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CES3063_Fall2020_rptSinifListesi.XLS.xlsx"
    poll_list_path = [
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport.csv - CSE3063_20201123_Mon_zoom_PollReport.csv (1).csv",
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport.csv - CSE3063_20201124_Tue_zoom_PollReport.csv.csv"]
    answer_keys = [
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201124_Tue_zoom_PollReport_AnswerKey.csv",
        r"C:\Users\ayberk\Desktop\dev\school\PythonProject\iteration1\CSE3063_20201123_Mon_zoom_PollReport_AnswerKey (1).csv"
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
        print("Welcome to the Poll analysis application")
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
        self.sevenPartAPollReport()

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
                # elif("Quiz" in questions.keys()):
                #     quizPolls = questions["Quiz"]
                #     student.attancePercent += 1
                #     continue
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
                        if(len(results) != len(poll.questions) -1):
                            for missed in range(len(results),len(poll.questions)-1):
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
                    row.append(sum(row[1:len(row) - 1]) / (len(row[1:len(row) - 1])))
                except:
                    print(row)
                    return
                sheet.append(row)
            df = pd.DataFrame(sheet)
            df.to_excel(excel_writer=newName)
            tempindex += 1
