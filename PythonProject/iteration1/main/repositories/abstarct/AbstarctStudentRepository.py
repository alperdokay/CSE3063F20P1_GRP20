from abc import ABC, abstractmethod


class AbstactRepository(ABC):
    @abstractmethod
    def createRepoByUniqueID(self,setofElements,nameOfUniqueElement):
        pass




