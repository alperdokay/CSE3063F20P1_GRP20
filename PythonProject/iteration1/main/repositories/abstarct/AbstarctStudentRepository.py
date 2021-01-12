from abc import ABC, abstractmethod


class AbstractRepository(ABC):
    @abstractmethod
    def createRepoByUniqueID(self, setOfElements, nameOfUniqueElement):
        pass




