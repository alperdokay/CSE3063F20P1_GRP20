class Factory():

    def create(self, typeOfClass, arguments):
        instance = typeOfClass(*arguments)
        return instance
