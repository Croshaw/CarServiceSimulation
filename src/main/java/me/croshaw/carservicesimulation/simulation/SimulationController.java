package me.croshaw.carservicesimulation.simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.*;
import me.croshaw.carservicesimulation.simulation.core.service.*;
import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;
import me.croshaw.carservicesimulation.simulation.core.util.adapters.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class SimulationController {
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .create();
    private static String[] surnames = new String[] { "Абрамов", "Авдеев", "Агеев", "Акимов", "Аксенов", "Александров", "Алексеев", "Алешин", "Андреев", "Андрианов", "Анисимов", "Антонов", "Артамонов", "Архипов", "Астафьев", "Афанасьев", "Балашов", "Баранов", "Белов", "Беляев", "Березин", "Беспалов", "Бирюков", "Блохин", "Богданов", "Борисов", "Бочаров", "Булгаков", "Буров", "Быков", "Васильев", "Виноградов", "Винокуров", "Власов", "Волков", "Воробьев", "Высоцкий", "Гаврилов", "Галкин", "Герасимов", "Голубев", "Горбачев", "Горбунов", "Горохов", "Горшков", "Грачев", "Григорьев", "Громов", "Губанов", "Гуляев", "Гусев", "Дементьев", "Демьянов", "Денисов", "Дмитриев", "Долгов", "Дроздов", "Дубровин", "Евдокимов", "Евсеев", "Егоров", "Еремин", "Ермаков", "Ермолов", "Ершов", "Ефимов", "Ефремов", "Жданов", "Журавлев", "Завьялов", "Зайцев", "Захаров", "Зеленин", "Зиновьев", "Золотарев", "Зубов", "Иванов", "Игнатов", "Игнатьев", "Ильин", "Ильинский", "Исаев", "Исаков", "Казаков", "Казанцев", "Калинин", "Карасев", "Карпов", "Карташов", "Киселев", "Клюев", "Ковалев", "Козлов", "Козырев", "Колесников", "Колосов", "Комаров", "Кондратьев", "Коновалов", "Константинов", "Корнев", "Корнеев", "Коровин", "Королев", "Костин", "Котов", "Кочетков", "Кошелев", "Круглов", "Крылов", "Кудрявцев", "Кузнецов", "Кузьмин", "Кулаков", "Кулешов", "Куликов", "Куприянов", "Лавров", "Лазарев", "Лапин", "Лебедев", "Левин", "Логинов", "Лопатин", "Лукьянов", "Лыков", "Львов", "Макаров", "Максимов", "Малышев", "Мальцев", "Маркелов", "Марков", "Мартынов", "Масленников", "Матвеев", "Медведев", "Мельников", "Мешков", "Мещеряков", "Миронов", "Митрофанов", "Михайлов", "Михеев", "Моргунов", "Морозов", "Муравьев", "Наумов", "Нестеров", "Никитин", "Никифоров", "Николаев", "Никонов", "Новиков", "Носков", "Носов", "Овсянников", "Овчинников", "Озеров", "Орехов", "Орлов", "Осипов", "Островский", "Павлов", "Панин", "Панков", "Панкратов", "Панов", "Пантелеев", "Панфилов", "Пахомов", "Петров", "Петровский", "Петухов", "Пименов", "Платонов", "Плотников", "Поздняков", "Покровский", "Поляков", "Пономарев", "Попов", "Потапов", "Прокофьев", "Пугачев", "Раков", "Рогов", "Родионов", "Рожков", "Романов", "Руднев", "Русаков", "Савин", "Сафонов", "Свиридов", "Седов", "Селиванов", "Семенов", "Сергеев", "Сидоров", "Симонов", "Синицын", "Скворцов", "Смирнов", "Соболев", "Соколов", "Соловьев", "Сомов", "Сорокин", "Сотников", "Софронов", "Стариков", "Старостин", "Степанов", "Субботин", "Суслов", "Сухарев", "Сычев", "Тарасов", "Терехов", "Тимофеев", "Титов", "Тихомиров", "Тихонов", "Трифонов", "Трошин", "Туманов", "Уткин", "Ушаков", "Фадеев", "Федоров", "Федотов", "Фетисов", "Филатов", "Филиппов", "Фомин", "Фролов", "Харитонов", "Царев", "Чернов", "Чистяков", "Шаповалов", "Шилов", "Широков", "Ширяев", "Шульгин", "Щербаков", "Щукин", "Юдин", "Яковлев" };
    private static String[] names = new String[] { "Адам", "Адриан", "Александр", "Алексей", "Али", "Альберт", "Анатолий", "Андрей", "Антон", "Аркадий", "Арсен", "Арсений", "Артемий", "Артур", "Артём", "Билал", "Богдан", "Борис", "Вадим", "Валерий", "Василий", "Виктор", "Виталий", "Владимир", "Владислав", "Всеволод", "Вячеслав", "Георгий", "Герман", "Глеб", "Гордей", "Григорий", "Давид", "Дамир", "Даниил", "Данил", "Данила", "Даниль", "Даниэль", "Демид", "Демьян", "Денис", "Дмитрий", "Евгений", "Егор", "Елисей", "Захар", "Ибрагим", "Иван", "Игнат", "Игорь", "Илья", "Камиль", "Карим", "Кирилл", "Клим", "Константин", "Лев", "Леон", "Леонид", "Лука", "Макар", "Максим", "Марат", "Марк", "Марсель", "Мартин", "Матвей", "Мирон", "Мирослав", "Михаил", "Назар", "Никита", "Николай", "Олег", "Павел", "Платон", "Пётр", "Рафаэль", "Роберт", "Родион", "Роман", "Ростислав", "Руслан", "Рустам", "Савва", "Савелий", "Святослав", "Семён", "Серафим", "Сергей", "Станислав", "Степан", "Стефан", "Тигран", "Тимофей", "Тимур", "Тихон", "Филипп", "Фёдор", "Эмиль", "Эмин", "Эмир", "Эрик", "Юрий", "Яков", "Ян", "Яромир", "Ярослав" };
    private static String[] patronymics = new String[] { "Адамович", "Александрович", "Алексеевич", "Алиевич", "Альбертович", "Андреевич", "Антонович", "Арсенович", "Арсентьевич", "Артемьевич", "Артурович", "Артёмович", "Билалович", "Богданович", "Вадимович", "Валерьевич", "Васильевич", "Викторович", "Витальевич", "Владимирович", "Владиславович", "Всеволодович", "Вячеславович", "Георгиевич", "Германович", "Глебович", "Гордеевич", "Григорьевич", "Давидович", "Дамирович", "Даниилович", "Данилович", "Даниэльевич", "Демидович", "Денисович", "Дмитриевич", "Евгеньевич", "Егорович", "Елисеевич", "Захарович", "Иванович", "Игоревич", "Ильич", "Кириллович", "Константинович", "Леонидович", "Леонович", "Лукич", "Львович", "Макарович", "Максимович", "Маратович", "Маркович", "Мартинович", "Матвеевич", "Миронович", "Мирославович", "Михайлович", "Никитич", "Николаевич", "Олегович", "Павлович", "Петрович", "Платонович", "Робертович", "Родионович", "Романович", "Русланович", "Савельевич", "Святославович", "Семёнович", "Сергеевич", "Станиславович", "Степанович", "Тимофеевич", "Тимурович", "Тихонович", "Филиппович", "Фёдорович", "Эмирович", "Эрикович", "Юрьевич", "Яковлевич", "Ярославович" };
    private static String[] brands = new String[] {"Lada", "BMW", "Mercedes", "Lambo"};
    private static String[] models = new String[] {"32", "23", "435", "123"};
    private final Brand[] brandArr;
    private transient Random random;
    private final int seed;
    private CarService carService;
    private boolean isPause;
    private transient Canvas canvas;
    private final Duration durationOfSimulation;
    private long secondsStep;
    private final ValueRange<Long> requestDistributionInterval;
    private LocalDateTime lastGeneratedRequest;
    private transient Timeline simulationTimeline;
    public SimulationController(LocalDateTime dateOfCreation, int seed, Duration durationOfSimulation, long secondsStep, ValueRange<Long> requestDistributionInterval
            , Duration maxServicingDuration, Duration maxWaitingDuration, double minSalary, float profitShare, Canvas canvas) {
        this.seed = seed;
        this.durationOfSimulation = durationOfSimulation;
        this.secondsStep = secondsStep;
        this.requestDistributionInterval = requestDistributionInterval;
        random = new Random(seed);
        lastGeneratedRequest = null;
        brandArr = getRandomBrands(random);
        carService = new CarService(dateOfCreation, maxServicingDuration, maxWaitingDuration, minSalary, profitShare);
        carService.setDrawer(5, 5, canvas.getWidth()-10, canvas.getHeight()-10);
        carService.setSpeed(0.1d);
        canvas.setOnMouseMoved(carService::mouseMovedEvent);
        generateWorkshops(carService, random);
        this.canvas = canvas;
        simulationTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.ONE, actionEvent -> carService.draw(this.canvas.getGraphicsContext2D())),
                new KeyFrame(javafx.util.Duration.ONE, actionEvent -> carService.drawTooltip(this.canvas.getGraphicsContext2D())),
                new KeyFrame(javafx.util.Duration.ONE, actionEvent -> clearBorder()),
                new KeyFrame(javafx.util.Duration.millis(20)),
                new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> tryGenerateRequest()),
                new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> work())
        );
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    private void clearBorder() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), 5);
        canvas.getGraphicsContext2D().clearRect(0, 0, 5, canvas.getHeight());
        canvas.getGraphicsContext2D().clearRect(canvas.getWidth()-5, 0, 5, canvas.getHeight());
        canvas.getGraphicsContext2D().clearRect(0, canvas.getWidth()-5, canvas.getWidth(), 5);
    }
    private void tryGenerateRequest() {
        if(!carService.isWork() || isDone() || !carService.isFixit() || isPause)
            return;
        if(lastGeneratedRequest == null) {
            lastGeneratedRequest = carService.getCurrentDateTime();
            carService.registerRequest(getRandomRequest(random, brandArr, lastGeneratedRequest, carService.getServices()));
            return;
        }
        long range = (long) (requestDistributionInterval.getRandomValueFromRange(random) * carService.getReputation());
        while(Duration.between(lastGeneratedRequest, carService.getCurrentDateTime()).toSeconds() >= range) {
            carService.registerRequest(getRandomRequest(random, brandArr, lastGeneratedRequest, carService.getServices()));
            lastGeneratedRequest = lastGeneratedRequest.plusSeconds(range);
            range = (long) (requestDistributionInterval.getRandomValueFromRange(random) * carService.getReputation());
        }
    }
    private void work() {
        if(!isDone() && carService.isFixit() && !isPause)
            carService.life(secondsStep);
    }
    public void stop() {
        simulationTimeline.stop();
    }
    public double getSpeed() {
        return carService.getSpeed();
    }
    public long getSecondsStep() {
        return secondsStep;
    }
    public void start() {
        simulationTimeline.play();
    }
    public void pause() {
        isPause = true;
    }
    public void resume() {
        isPause = false;
    }
    public boolean isDone() {
        return carService.getLifeDuration().compareTo(durationOfSimulation) >= 0;
    }
    public static Request getRandomRequest(Random random, Brand[] brandsArr, LocalDateTime dateOfCreation, Service[] services) {
        int cServices = random.nextInt(1, services.length+1);
        var brand = brandsArr[random.nextInt(0, brandsArr.length)];
        Request request = new Request(dateOfCreation, new Car(brand, brand.getRandomModel(random), getRandomPeople(random)));
        for(int i = 0 ; i < cServices; i++) {
            if(!request.addWorkServicing(services[random.nextInt(0, services.length)], random))
                i--;
        }
        return request;
    }
    public static People getRandomPeople(Random random) {
        return new People(surnames[random.nextInt(0, surnames.length)]
                , names[random.nextInt(0, names.length)]
                , patronymics[random.nextInt(0, patronymics.length)]);
    }
    public static Brand[] getRandomBrands(Random random) {
        int c = random.nextInt(1, brands.length+1);
        Brand[] brands1 = new Brand[c];
        for(int i = 0; i < c; i++) {
            brands1[i] = new Brand(brands[random.nextInt(0, brands.length)]);
            int tc = random.nextInt(1, models.length);
            for(int j = 0; j < tc; j++) {
                brands1[i].addModel(models[random.nextInt(0, models.length)]);
            }
        }
        return brands1;
    }
    public static void generateMasters(Workshop workshop, Random random) {
        int cMasters = random.nextInt(2, 8);
        for(int i = 0; i < cMasters; i++)
            workshop.hire(surnames[random.nextInt(0, surnames.length)]
                    , names[random.nextInt(0, names.length)]
                    , patronymics[random.nextInt(0, patronymics.length)] );
    }
    public static void generateWorkshops(CarService carService, Random random) {
        generateMasters(carService.registerService(new BodyRepairService()), random);
        generateMasters(carService.registerService(new TireService()), random);
        generateMasters(carService.registerService(new InspectionService()), random);
        generateMasters(carService.registerService(new EngineRepairService()), random);
    }
    public void setStep(long secondsStep) {
        this.secondsStep = secondsStep;
    }
    public void setSpeed(double value) {
        carService.setSpeed(value);
    }
    public static String serialize(SimulationController simulationController) {
        return gson.toJson(simulationController);
    }
    public static SimulationController deserialize (String json) {
        return gson.fromJson(json, SimulationController.class);
    }

    public void setCanvas(Canvas newCanvas) {
        canvas = newCanvas;
        if(random == null)
            random = new Random(seed);
        if(simulationTimeline == null) {
            simulationTimeline = new Timeline(
                    new KeyFrame(javafx.util.Duration.ONE, actionEvent -> carService.draw(this.canvas.getGraphicsContext2D())),
                    new KeyFrame(javafx.util.Duration.ONE, actionEvent -> carService.drawTooltip(this.canvas.getGraphicsContext2D())),
                    new KeyFrame(javafx.util.Duration.ONE, actionEvent -> clearBorder()),
                    new KeyFrame(javafx.util.Duration.millis(20)),
                    new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> tryGenerateRequest()),
                    new KeyFrame(javafx.util.Duration.ZERO, actionEvent -> work())
            );
            simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        }
        lastGeneratedRequest = null;
        canvas.setOnMouseMoved(carService::mouseMovedEvent);
        carService.load();
    }
}
