package com.itvillage.renttech.base.seed;

import com.github.javafaker.Faker;
import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.category.CategoryRepository;
import com.itvillage.renttech.dynamicform.DynamicFormQuestion;
import com.itvillage.renttech.dynamicform.DynamicFormQuestionRepository;
import com.itvillage.renttech.dynamicform.InputType;
import com.itvillage.renttech.dynamicform.PurposeType;
import com.itvillage.renttech.dynamicform.QuestionOption;
import com.itvillage.renttech.dynamicform.QuestionType;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.dynamicform.UserAnswerValue;
import com.itvillage.renttech.rentalpost.ProcessingStatus;
import com.itvillage.renttech.rentalpost.RentalPost;
import com.itvillage.renttech.rentalpost.RentalPostRepository;
import com.itvillage.renttech.verification.user.Gender;
import com.itvillage.renttech.verification.user.Profession;
import com.itvillage.renttech.verification.user.Role;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DataSeedBootstrap.SeedProperties.class)
public class DataSeedBootstrap {

    private static final String[] BD_DIVISIONS = {
            "Dhaka", "Chattogram", "Sylhet", "Khulna",
            "Rajshahi", "Barishal", "Rangpur", "Mymensingh"
    };
    private static final String[] BD_ZILLAS = {
            "Dhaka", "Gazipur", "Narayanganj", "Cumilla", "Chattogram",
            "Sylhet", "Khulna", "Rajshahi", "Bogura", "Mymensingh"
    };
    private static final String[] BD_THANAS = {
            "Dhanmondi", "Gulshan", "Banani", "Mirpur", "Uttara",
            "Mohammadpur", "Tejgaon", "Motijheel", "Bashundhara", "Wari"
    };

    private static final String[] CATEGORY_NAMES = {
            "Apartments", "Houses", "Studio Flats", "Shared Rooms", "Office Spaces",
            "Warehouses", "Shop Spaces", "Event Venues", "Cars", "SUVs",
            "Motorcycles", "Scooters", "Bicycles", "Boats", "Auto Rickshaws",
            "Laptops", "Cameras", "Projectors", "Audio Equipment", "Gaming Consoles",
            "Drones", "Tablets", "Power Tools", "Generators", "Construction Equipment",
            "Gardening Tools", "Cleaning Equipment", "Wedding Dresses", "Suits and Tuxedos", "Costumes",
            "Jewelry", "Watches", "Musical Instruments", "Camping Gear", "Tents",
            "Sports Equipment", "Fitness Equipment", "Kayaks", "Surf Boards", "Books",
            "Board Games", "Furniture Sets", "Home Appliances", "Refrigerators", "Air Conditioners",
            "Washing Machines", "Wedding Photography", "Party Supplies", "Sound Systems", "Stage Lighting"
    };

    private static final List<QuestionTemplate> QUESTION_TEMPLATES = List.of(
            new QuestionTemplate("Condition",     QuestionType.DROPDOWN, null,               List.of("New", "Like New", "Used", "Refurbished"), PurposeType.SPECIFICATION),
            new QuestionTemplate("Color",         QuestionType.INPUT,    InputType.TEXT,     List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Brand",         QuestionType.INPUT,    InputType.TEXT,     List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Year",          QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Size",          QuestionType.DROPDOWN, null,               List.of("Small", "Medium", "Large", "Extra Large"), PurposeType.SPECIFICATION),
            new QuestionTemplate("Material",      QuestionType.INPUT,    InputType.TEXT,     List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Description",   QuestionType.TEXTAREA, null,               List.of(), PurposeType.OTHERS),
            new QuestionTemplate("Features",      QuestionType.CHECKBOX, null,               List.of("WiFi", "Bluetooth", "Touchscreen", "Battery Powered", "Waterproof"), PurposeType.SPECIFICATION),
            new QuestionTemplate("Bedrooms",      QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Bathrooms",     QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Floor Number",  QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Furnished",     QuestionType.RADIO,    null,               List.of("Yes", "No", "Partially"), PurposeType.SPECIFICATION),
            new QuestionTemplate("Square Feet",   QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Capacity",      QuestionType.INPUT,    InputType.NUMERIC,  List.of(), PurposeType.SPECIFICATION),
            new QuestionTemplate("Pet Friendly",  QuestionType.RADIO,    null,               List.of("Yes", "No"), PurposeType.SPECIFICATION),
            new QuestionTemplate("Amenities",     QuestionType.CHECKBOX, null,               List.of("AC", "Heater", "Parking", "Pool", "Gym"), PurposeType.AMENITIES)
    );

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RentalPostRepository rentalPostRepository;
    private final DynamicFormQuestionRepository dfqRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager txManager;
    private final SeedProperties properties;

    @PersistenceContext
    private EntityManager em;

    // Seed-time ID generator. MagicBaseModel.onPrePersist() generates only 8-digit random IDs,
    // which collide heavily at 350K+ rows (birthday paradox). We pre-assign IDs here using a
    // run-unique timestamp prefix + monotonic counter so the seeded rows never collide with
    // each other and never collide with the 8-digit IDs used in production.
    private final String seedIdPrefix = "S" + System.currentTimeMillis();
    private final AtomicLong seedIdCounter = new AtomicLong();

    private String nextSeedId() {
        return seedIdPrefix + "-" + seedIdCounter.incrementAndGet();
    }

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            if (!properties.isEnabled()) {
                log.info("Init data disabled (set application.seed.enabled=true to seed)");
                return;
            }

            long start = System.currentTimeMillis();
            Faker faker = new Faker(Locale.ENGLISH);
            log.info("=== Init data starting ===");

            List<String> userIds = ensureUsers(faker);
            List<Category> categories = seedCategories();
            List<String> postIds = seedRentalPosts(faker, userIds, categories);
            seedInterests(userIds, postIds);

            log.info("=== Init data complete in {} ms ===", System.currentTimeMillis() - start);
        };
    }

    // ---------- Users (batched) ----------

    private List<String> ensureUsers(Faker faker) {
        int existing = (int) userRepository.findAllByRole(Role.USER, PageRequest.of(0, 1)).getTotalElements();
        int target = properties.getUsersCount();
        int toCreate = Math.max(0, target - existing);
        int batchSize = properties.getBatchSize();

        for (int processed = 0; processed < toCreate; processed += batchSize) {
            int remaining = Math.min(batchSize, toCreate - processed);
            int batchStart = processed;
            inTx(() -> {
                for (int i = 0; i < remaining; i++) {
                    String mobileNo = randomBdMobile();
                    if (userRepository.findByMobileNo(mobileNo).isPresent()) continue;

                    User u = new User();
                    u.setId(nextSeedId());
                    u.setName(faker.name().fullName());
                    u.setMobileNo(mobileNo);
                    u.setGender(randomEnum(Gender.class));
                    u.setNidNumber(faker.number().digits(10));
                    u.setPresentAddress(faker.address().fullAddress());
                    u.setProfession(randomEnum(Profession.class));
                    u.setUniversityName(faker.university().name());
                    u.setCurrentCoins(ThreadLocalRandom.current().nextInt(0, 1000));
                    u.setPassword(passwordEncoder.encode(properties.getDefaultPassword()));
                    u.setRole(Role.USER);
                    em.persist(u);
                }
                em.flush();
                em.clear();
            });
            log.info("Users — {} / {} created", Math.min(batchStart + remaining, toCreate), toCreate);
        }

        List<String> ids = userRepository.findAllByRole(Role.USER, PageRequest.of(0, target)).stream()
                .map(User::getId).toList();
        log.info("Users — existing before={}, attempted={}, final usable={}", existing, toCreate, ids.size());
        return ids;
    }

    // ---------- Categories + dynamic questions ----------

    private List<Category> seedCategories() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int target = Math.min(properties.getCategoriesCount(), CATEGORY_NAMES.length);
        List<Category> result = new ArrayList<>(target);

        List<Category> existingAll = categoryRepository.findAll();
        Map<String, Category> existingByName = new HashMap<>();
        for (Category c : existingAll) existingByName.put(c.getName().toLowerCase(), c);

        for (int i = 0; i < target; i++) {
            String name = CATEGORY_NAMES[i];
            Category existing = existingByName.get(name.toLowerCase());
            if (existing != null) {
                result.add(existing);
                continue;
            }

            int idx = i;
            inTx(() -> {
                Category category = new Category();
                category.setName(name);
                category.setDescription("Rent " + name + " at affordable prices across Bangladesh");
                category.setActive(true);
                em.persist(category);
                em.flush();

                createSysQuestions(category);
                createRandomQuestions(category, rnd);
                em.flush();

                int qCount = dfqRepository.findAllByCategoryIdOrderByPositionAsc(category.getId()).size();
                log.info("Inserted category [{}/{}] id={} name={} questions={}",
                        idx + 1, target, category.getId(), category.getName(), qCount);
                result.add(category);
                em.clear();
            });
        }
        log.info("Categories — total in seed set: {}", result.size());
        return result;
    }

    private void createSysQuestions(Category c) {
        String suffix = c.getId() != null && c.getId().length() >= 4 ? c.getId().substring(4) : c.getId();
        saveQuestion(c, ApiConstant.SYS_PRICE_QS_ + suffix,          "Price",          QuestionType.INPUT, InputType.DECIMAL, PurposeType.OTHERS, "Enter your price",     1, true,  List.of());
        saveQuestion(c, ApiConstant.SYS_LOCATION_QS_ + suffix,       "Location",       QuestionType.INPUT, InputType.TEXT,    PurposeType.OTHERS, "Enter lat,long",       2, true,  List.of());
        saveQuestion(c, ApiConstant.SYS_TITLE_QS_ + suffix,          "Post Title",     QuestionType.INPUT, InputType.TEXT,    PurposeType.OTHERS, "E.g 3 Bedroom Flat",   3, false, List.of());
        saveQuestion(c, ApiConstant.SYS_AVAILABLE_FROM_QS_ + suffix, "Available From", QuestionType.INPUT, InputType.DATE,    PurposeType.OTHERS, "Enter Available Date", 5, true,  List.of());
    }

    private void createRandomQuestions(Category c, ThreadLocalRandom rnd) {
        List<QuestionTemplate> pool = new ArrayList<>(QUESTION_TEMPLATES);
        Collections.shuffle(pool);
        int n = rnd.nextInt(3, 8);
        int position = 10;
        for (int i = 0; i < n && i < pool.size(); i++) {
            QuestionTemplate t = pool.get(i);
            saveQuestion(c, null, t.label, t.questionType, t.inputType, t.purposeType,
                    t.label, position++, false, t.options);
        }
    }

    private void saveQuestion(Category c, String fixedId, String label, QuestionType qType, InputType inType,
                              PurposeType pType, String placeholder, int position, boolean required,
                              List<String> optionNames) {
        DynamicFormQuestion q = new DynamicFormQuestion();
        if (fixedId != null) q.setId(fixedId);
        q.setCategory(c);
        q.setLabel(label);
        q.setQuestionType(qType);
        q.setInputType(inType);
        q.setPurposeType(pType);
        q.setPlaceHolder(placeholder);
        q.setPosition(position);
        q.setQsRequired(required);

        if (!optionNames.isEmpty()) {
            Set<QuestionOption> options = new HashSet<>();
            int value = 1;
            for (String n : optionNames) {
                QuestionOption o = new QuestionOption();
                o.setName(n);
                o.setValue(value++);
                o.setQuestion(q);
                options.add(o);
            }
            q.setDefaultOptions(options);
        }
        em.persist(q);
    }

    // ---------- Rental posts (batched) ----------

    private List<String> seedRentalPosts(Faker faker, List<String> userIds, List<Category> categories) {
        int target = properties.getPostsCount();
        if (target <= 0 || userIds.isEmpty() || categories.isEmpty()) {
            log.warn("Skip post seeding — target={} users={} categories={}",
                    target, userIds.size(), categories.size());
            return List.of();
        }

        // Pre-cache questions per category (one query each, reused across all posts of that category).
        // Wrap in a single transaction and force-initialize the lazy defaultOptions collection so the
        // cached entities can be safely read after detach in subsequent per-batch transactions.
        Map<String, List<DynamicFormQuestion>> questionsByCategory = new HashMap<>();
        inTx(() -> {
            for (Category c : categories) {
                List<DynamicFormQuestion> qs = dfqRepository.findAllByCategoryIdOrderByPositionAsc(c.getId());
                for (DynamicFormQuestion q : qs) {
                    q.getDefaultOptions().size();
                }
                questionsByCategory.put(c.getId(), qs);
            }
        });

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int batchSize = properties.getBatchSize();
        List<String> createdIds = new ArrayList<>(target);

        for (int processed = 0; processed < target; processed += batchSize) {
            int chunkSize = Math.min(batchSize, target - processed);
            inTx(() -> {
                List<RentalPost> batch = new ArrayList<>(chunkSize);
                for (int j = 0; j < chunkSize; j++) {
                    Category cat = categories.get(rnd.nextInt(categories.size()));
                    User owner = em.getReference(User.class, userIds.get(rnd.nextInt(userIds.size())));
                    List<DynamicFormQuestion> questions = questionsByCategory.get(cat.getId());

                    RentalPost post = buildBasicPost(cat, owner, faker, rnd);
                    attachFormAnswers(post, questions, faker, rnd);
                    em.persist(post);
                    batch.add(post);
                }
                em.flush();
                for (RentalPost p : batch) createdIds.add(p.getId());
                em.clear();
            });

            int done = processed + chunkSize;
            if (done % (batchSize * 5) == 0 || done == target) {
                log.info("  posts ... {} / {}", done, target);
            }
        }
        log.info("Rental posts — created {}", createdIds.size());
        return createdIds;
    }

    private RentalPost buildBasicPost(Category category, User owner, Faker faker, ThreadLocalRandom rnd) {
        String thana = BD_THANAS[rnd.nextInt(BD_THANAS.length)];
        RentalPost post = new RentalPost();
        post.setId(nextSeedId());
        post.setOwner(owner);
        post.setCategory(em.getReference(Category.class, category.getId()));
        post.setName(faker.commerce().productName() + " in " + thana);
        post.setPrice(String.valueOf(rnd.nextInt(3_000, 50_000)));
        post.setPriceLabel("Monthly Rent");
        post.setAvailableFrom(LocalDate.now().plusDays(rnd.nextInt(1, 60)).toString());
        post.setAvailableFromLabel("Available From");
        post.setAddress(faker.address().streetAddress());
        post.setDivision(BD_DIVISIONS[rnd.nextInt(BD_DIVISIONS.length)]);
        post.setZilla(BD_ZILLAS[rnd.nextInt(BD_ZILLAS.length)]);
        post.setThanaOrUpazila(thana);
        post.setLatitude(round6(rnd.nextDouble(20.7, 26.5)));
        post.setLongitude(round6(rnd.nextDouble(88.1, 92.6)));
        post.setValid(true);
        post.setExpiryDate(ZonedDateTime.now(ZoneId.of("UTC")).plusDays(rnd.nextInt(30, 91)));
        post.setProcessingStatus(ProcessingStatus.READY);
        return post;
    }

    private void attachFormAnswers(RentalPost post, List<DynamicFormQuestion> questions,
                                   Faker faker, ThreadLocalRandom rnd) {
        Set<UserAnswerDFormQuestion> answers = new HashSet<>();
        for (DynamicFormQuestion q : questions) {
            UserAnswerDFormQuestion qAnswer = new UserAnswerDFormQuestion();
            qAnswer.setId(nextSeedId());
            qAnswer.setDynamicFormQuestion(em.getReference(DynamicFormQuestion.class, q.getId()));

            Set<UserAnswerValue> values = new HashSet<>();
            for (UserAnswerValue v : generateValues(q, post, faker, rnd)) {
                v.setQuestion(qAnswer);
                values.add(v);
            }
            if (values.isEmpty()) continue;
            qAnswer.setAnswers(values);
            answers.add(qAnswer);
        }
        post.setFormQuestionsAnswer(answers);
    }

    private List<UserAnswerValue> generateValues(DynamicFormQuestion q, RentalPost post,
                                                 Faker faker, ThreadLocalRandom rnd) {
        String qId = q.getId();
        if (qId != null) {
            if (qId.startsWith(ApiConstant.SYS_TITLE_QS_))          return List.of(value(post.getName()));
            if (qId.startsWith(ApiConstant.SYS_PRICE_QS_))          return List.of(value(post.getPrice()));
            if (qId.startsWith(ApiConstant.SYS_LOCATION_QS_))       return List.of(value(post.getLatitude() + "," + post.getLongitude()));
            if (qId.startsWith(ApiConstant.SYS_AVAILABLE_FROM_QS_)) return List.of(value(post.getAvailableFrom()));
        }

        QuestionType type = q.getQuestionType() == null ? QuestionType.INPUT : q.getQuestionType();
        Set<QuestionOption> options = q.getDefaultOptions();

        switch (type) {
            case DROPDOWN:
            case RADIO: {
                if (options == null || options.isEmpty()) return List.of(value(faker.lorem().word()));
                QuestionOption pick = pickOne(options, rnd);
                return List.of(value(pick.getName(), em.getReference(QuestionOption.class, pick.getId())));
            }
            case CHECKBOX: {
                if (options == null || options.isEmpty()) return List.of();
                List<QuestionOption> shuffled = new ArrayList<>(options);
                Collections.shuffle(shuffled);
                int n = Math.min(shuffled.size(), 1 + rnd.nextInt(Math.min(3, shuffled.size())));
                List<UserAnswerValue> out = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    QuestionOption opt = shuffled.get(i);
                    out.add(value(opt.getName(), em.getReference(QuestionOption.class, opt.getId())));
                }
                return out;
            }
            case TEXTAREA:
                return List.of(value(faker.lorem().paragraph()));
            case INPUT:
            default: {
                InputType in = q.getInputType();
                if (in == InputType.NUMERIC) return List.of(value(String.valueOf(rnd.nextInt(1, 100))));
                if (in == InputType.DECIMAL) return List.of(value(String.format("%.2f", rnd.nextDouble(1, 100))));
                if (in == InputType.DATE)    return List.of(value(LocalDate.now().plusDays(rnd.nextInt(0, 30)).toString()));
                return List.of(value(faker.lorem().sentence()));
            }
        }
    }

    // ---------- Interests (batched, via native join-table insert) ----------

    private void seedInterests(List<String> userIds, List<String> postIds) {
        int target = properties.getInterestsCount();
        if (target <= 0 || userIds.isEmpty() || postIds.isEmpty()) return;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int interestablePosts = Math.min(target, postIds.size());
        List<String> shuffled = new ArrayList<>(postIds);
        Collections.shuffle(shuffled);

        // Build all (post_id, user_id) pairs up-front
        List<String[]> pairs = new ArrayList<>(interestablePosts * 3);
        for (int i = 0; i < interestablePosts; i++) {
            String postId = shuffled.get(i);
            int n = 1 + rnd.nextInt(5);
            Set<String> seen = new HashSet<>();
            for (int j = 0; j < n; j++) {
                String uid = userIds.get(rnd.nextInt(userIds.size()));
                if (seen.add(uid)) pairs.add(new String[]{postId, uid});
            }
        }

        // Bulk insert into the join table. Much faster than reattaching each post.
        int batchSize = properties.getBatchSize();
        int total = pairs.size();
        for (int processed = 0; processed < total; processed += batchSize) {
            int end = Math.min(processed + batchSize, total);
            List<String[]> chunk = pairs.subList(processed, end);
            inTx(() -> {
                StringBuilder sql = new StringBuilder(
                        "INSERT INTO rental_post_interest (rental_post_id, user_id) VALUES ");
                for (int i = 0; i < chunk.size(); i++) {
                    if (i > 0) sql.append(',');
                    sql.append("(?, ?)");
                }
                jakarta.persistence.Query q = em.createNativeQuery(sql.toString());
                int p = 1;
                for (String[] pair : chunk) {
                    q.setParameter(p++, pair[0]);
                    q.setParameter(p++, pair[1]);
                }
                q.executeUpdate();
            });
            int done = end;
            if (done % (batchSize * 5) == 0 || done == total) {
                log.info("  interests ... {} / {} rows", done, total);
            }
        }
        log.info("Interests — inserted {} join-table rows for {} posts", total, interestablePosts);
    }

    // ---------- Helpers ----------

    private void inTx(Runnable work) {
        new TransactionTemplate(txManager).executeWithoutResult(s -> work.run());
    }

    private UserAnswerValue value(String text) {
        return value(text, null);
    }

    private UserAnswerValue value(String text, QuestionOption option) {
        UserAnswerValue v = new UserAnswerValue();
        v.setId(nextSeedId());
        v.setAnswer(text == null ? "" : text);
        v.setQuestionOption(option);
        return v;
    }

    private static QuestionOption pickOne(Set<QuestionOption> options, ThreadLocalRandom rnd) {
        int idx = rnd.nextInt(options.size());
        int i = 0;
        for (QuestionOption o : options) {
            if (i++ == idx) return o;
        }
        return options.iterator().next();
    }

    private static String randomBdMobile() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int operator = r.nextInt(3, 10);
        StringBuilder sb = new StringBuilder("01").append(operator);
        for (int i = 0; i < 8; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    private static <E extends Enum<E>> E randomEnum(Class<E> clazz) {
        E[] values = clazz.getEnumConstants();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    private static double round6(double v) {
        return Math.round(v * 1_000_000d) / 1_000_000d;
    }

    // ---------- Config ----------

    @Data
    @ConfigurationProperties(prefix = "application.seed")
    public static class SeedProperties {
        private boolean enabled = false;
        private int usersCount = 5000;
        private int categoriesCount = 50;
        private int postsCount = 50_000;
        private int interestsCount = 20_000;
        private int batchSize = 100;
        private String defaultPassword = "password123";
    }

    private record QuestionTemplate(
            String label,
            QuestionType questionType,
            InputType inputType,
            List<String> options,
            PurposeType purposeType
    ) {}
}
