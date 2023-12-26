```
@startuml
class People {
- surname : String
- name : String
- patronymic : String
+ getSurname() : String
+ getName() : String
+ getPatronymic() : String
+ toString() : String
+ toShortString() : String
}
class Brand {
- name : String
- models : String[]
+ addModel(name : String) : void
+ getName() : String
+ getModels() : String[]
+ getRandomModel(random : Random) : String
+ toString() : String
}
class Car {
- brand : Brand
- model : String
- owner : People
- isBusy : Boolean
+ getBrand() : Brand
+ getModel() : Model
+ getOwner() : People
+ isBusy() : Boolean
+ setBusy(value : boolean) : void
+ toString() : String
}
class Service {
- name : String
- averageDurationOfService : Duration
- offsetServiceingRange : ValueRange<Long>
- price : Double
+ getName() : String
+ getAverageDurationOfService() : Duration
+ getOffsetServicingRange() : ValueRange<Long>
+ getPrice() : Double
+ toString() : String
+ {static} getRandomDurationOfService(random : Random) : Duration
}
class CreatedInfo {
- dateTimeOfCreation : LocalDateTime
+ getDateTimeOfCreation() : LocalDateTime
+ getDateOfCreation() : LocalDate
+ getTimeOfCreation() : LocalTime
+ toString() : String
}
class WorkServicing {
- service : Service
- car : Car
- master : Master
- expectedServiceDuration : Duration
- workDuration : Duration
- waitingDuration : Duration
- isDropped : Boolean
+ getMaster() : Master
+ drop() : void
+ setMaster(master : Master) : void
+ service(secondsStep : Long) : Long
+ waiting(secondsStep : Long) : Long
+ getPrice() : Double
+ getWaitingDuration() : Duration
+ getWorkDuration() : Duration
+ getExpectedServiceDurationDuration() : Duration
+ getService() : Service
+ isDone() : Boolean
+ isCarBusy() : Boolean
+ toString() : String
}
class Request {
- workServicingList : ArrayList<WorkServicing>
- car : Car
+ addWorkServicing(service : Service, random : Random) : Boolean
+ addWorkServicing(workServicing : WorkServicing) : Boolean
+ waiting() : void
+ isServiceAlreadyExists() : Boolean
+ removeWorkServicing(id : Integer) : WorkServicing
+ removeWorkServicing(workServicing : WorkServicing) : WorkServicing
+ getCar() : Car
+ getWaitingDuration() : Duration
+ getExpectedServiceDuration() : Duration
+ getWorkDuration() : Duration
+ getWorkServicing() : ArrayList<WorkServicing>
+ isDone() : Boolean
+ isDropped() : Boolean
+ toString() : String
}
class Master {
- currentTask : WorkServicing
- maxServicingDuration : Duration
- maxWaitingDuration : Duration
- completedWorkServicingMap : Map<LocalDate, ArrayList<WorkServicing>>
- dropWorkServicingMap : Map<LocalDate, ArrayList<WorkServicing>>
- employmentMap : Map<LocalDate, Duration>
- minSalary : Double
- profitShare : Float
+ setTask(workServicing : WorkServicing) : void
+ checkToDrop(date : LocalDate) : void
+ work(secondsStep : Long) : Long
+ getAverageWaitingDuration() : Duration
+ getAverageEmployementDuration() : Duration
+ getNumberOfCompletedWorkServicing() : Integer
+ getNumberOfDropWorkServicing() : Integer
+ getTotalProfitByDate(date : LocalDate) : Double
+ getTotalSalaryByDate(date : LocalDate) : Double
+ getTotalProfit() : Double
+ getTotalSalary() : Double
+ getCurrentDateTime() : LocalDateTime
+ getCurrentDate() : LocalDate
+ getCurrentTime() : LocalTime
+ toString() : String
}
class Workshop {
- service : Service
- name : String
- masters : HashSet<Master>
- queue : Queue<WorkServicing>
- queueLength : Map<LocalDate, Pair<Integer, Integer>>
- dropWorkServicingMap : Map<LocalDate, ArrayList<WorkServicing>>
- maxServicingDuration : Duration
- maxWaitingDuration : Duration
- currentLifeDuration : Duration
- minSalary : Double
- profitShare : Float
- maxMasters : int
+ hire(master : Master) : Boolean
+ hire(surname : String, name : String, patronymic : String) : Boolean
+ fire(master : Master) : Boolean
+ registerWorkServicing(workServicing : WorkServicing) : Boolean
+ checkToDrop() : void
+ tryToTakeTaskByMaster(master : Master) : Boolean
+ work(secondsStep : Long) : void
+ life(secondsStep : Long) : void
+ getMasters() : Master[]
+ getAverageWaitingDuration() : Duration
+ getAverageEmployementDuration() : Duration
+ getNumberOfCompletedWorkServicing() : Integer
+ getNumberOfDropWorkServicing() : Integer
+ getTotalProfitByDate(date : LocalDate) : Double
+ getMastersTotalSalaryByDate(date : LocalDate) : Double
+ getTotalProfit() : Double
+ getMastersTotalSalary() : Double
+ getCurrentDateTime() : LocalDateTime
+ getCurrentDate() : LocalDate
+ getCurrentTime() : LocalTime
+ getAverageQueueLengthByDate(date : LocalDate) : Integer
+ getAverageQueueLength() : Integer
+ getMaxQueueLengthByDate(date : LocalDate) : Integer
+ getMaxQueueLength() : Integer
+ getMinQueueLengthByDate(date : LocalDate) : Integer
+ getMinQueueLength() : Integer
+ toString() : String
}
class CarService {
- workshops : HashSet<Workshop<?>>
- workSchedule : Map<DayOfWeek, Pair<LocalTime, Duration>>
- completedRequestsMap : Map<LocalDate, ArrayList<Request>>
- dropRequestsMap : Map<LocalDate, ArrayList<Request>>
- queueLength : Map<LocalDate, Pair<Integer, Integer>>
- queue : HashSet<Request>
- currentLifeDuration : Duration
- services : HashSet<Service>
- maxServicingDuration : Duration
- maxWaitingDuration : Duration
- minSalary : Double
- profitShare : Float
- reputation : Double
+ registerRequest(request : Request) : Boolean
+ registerService(service : Service) : Workshop
+ checkToDrop() : void
+ checkToComplete() : void
+ work(secondsStep : Long) : void
+ life(secondsStep : Long) : void
+ getAverageWaitingDuration() : Duration
+ getAverageEmployementDuration() : Duration
+ getNumberOfCompletedRequests() : Integer
+ getNumberOfDropRequests() : Integer
+ getNumberOfCompletedWorkServicing() : Integer
+ getNumberOfDropWorkServicing() : Integer
+ getTotalProfitByDate(date : LocalDate) : Double
+ getMastersTotalSalaryByDate(date : LocalDate) : Double
+ getTotalProfit() : Double
+ getMastersTotalSalary() : Double
+ getWorkshopByService(service : Service) : Workshop
+ getAverageQueueLengthByDate(date : LocalDate) : Integer
+ getAverageQueueLength() : Integer
+ getMaxQueueLengthByDate(date : LocalDate) : Integer
+ getMaxQueueLength() : Integer
+ getMinQueueLengthByDate(date : LocalDate) : Integer
+ getMinQueueLength() : Integer
+ getCurrentDateTime() : LocalDateTime
+ getCurrentDate() : LocalDate
+ getCurrentTime() : LocalTime
+ getReputation() : Double
+ isWork() : Boolean
+ isWork(dateTime : LocalDateTime) : Boolean
+ getServices() : Service[]
+ getLifeDuration() : Duration
+ toString() : String
}
People <|-- Master
Car *-- Brand
Car --> People : Проверить
CreatedInfo <|-- CarService
CreatedInfo <|-- Request
CreatedInfo <|-- Workshop
CreatedInfo <|-- WorkServicing
WorkServicing --> Service : Проверить
WorkServicing <--> Master : Проверить
WorkServicing --> Car : Проверить
Request --> Car : Проверить
Request o-- WorkServicing
Workshop o-- Master
Workshop --> Service : Проверить
Workshop o-- WorkServicing
CarService *-- Workshop
CarService *-- Request
CarService *-- Service
@enduml
```