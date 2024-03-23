//package com.supreme.utility;
//
//import com.supreme.entity.*;
//import com.supreme.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.logging.Logger;
//
//
//@Component
//public class DummyDataLoader implements CommandLineRunner {
//
//    private static final String PHONE_1 = "9876543210", PHONE_2 = "9876543211", PHONE_3 = "9876543212", PHONE_4 = "9876543213";
//    private static final String FN_1 = "Abdul", FN_2 = "Albert", FN_3 = "Elon", FN_4 = "Narendra";
//    private static final String LN_1 = "Kalam", LN_2 = "Einstein", LN_3 = "Musk", LN_4 = "Modi";
//    private static final String PICNAME_1 = "abdul-kalam.jpg", PICNAME_2 = "albert-einstein.jpeg", PICNAME_3 = "elon-musk.jpeg", PICNAME_4 = "narendra-modi.jpg";
//    private static final String DISTRIBUTOR_PICURL_1 = "/distributor/download/9876543210", DISTRIBUTOR_PICURL_2 = "/distributor/download/9876543211";
//    private static final String EXECUTIVE_PICURL_1 = "/executive/download/9876543212", EXECUTIVE_PICURL_2 = "/executive/download/9876543213";
//    private final AdminProfileRepo adminProfileRepo;
//    private final DistributorProfileRepo distributorProfileRepo;
//    private final DistributorProductQtyRepo dPQtyRepo;
//    private final ExecutiveProfileRepo executiveProfileRepo;
//    private final UserRepository userRepository;
//    private final CategoryRepo categoryRepo;
//    private final ProductRepo productRepo;
//    private final OutletRepo outletRepo;
//    private final AppFeaturesRepo appFeaturesRepo;
//    private final PasswordEncoder encoder;
//    String pin = "123456";
//    Logger logger = Logger.getLogger(getClass().getName());
//
//    @Autowired
//    public DummyDataLoader(AdminProfileRepo adminProfileRepo, DistributorProfileRepo distributorProfileRepo, DistributorProductQtyRepo dPQtyRepo, ExecutiveProfileRepo executiveProfileRepo, UserRepository userRepository, CategoryRepo categoryRepo, ProductRepo productRepo, OutletRepo outletRepo, AppFeaturesRepo appFeaturesRepo, PasswordEncoder encoder) {
//        this.adminProfileRepo = adminProfileRepo;
//        this.distributorProfileRepo = distributorProfileRepo;
//        this.dPQtyRepo = dPQtyRepo;
//        this.executiveProfileRepo = executiveProfileRepo;
//        this.userRepository = userRepository;
//        this.categoryRepo = categoryRepo;
//        this.productRepo = productRepo;
//        this.outletRepo = outletRepo;
//        this.appFeaturesRepo = appFeaturesRepo;
//        this.encoder = encoder;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Add dummy data to the database
//        addAppFeatures();
//        addDummyCategoriesProducts();
//        addDummyUserData();
//        addOutlets();
//    }
//
//    private void addAppFeatures() {
//        appFeaturesRepo.save(new AppFeatures(1L, false, false, true, true, false, false, false, false, false, false, false, false, false, true, false, true));
//        logger.info("App Features added Successfully");
//    }
//
//    public void addDummyCategoriesProducts() {
//        Category panMasala = new Category(1L, "Pan Masala");
//        Category spices = new Category(2L, "Spices");
//
//        Product panMasalaPouch = new Product(1L, "Pan Masala", "panmasala.png", "/admin/product/download/Pan Masala", panMasala);
//        Product corianderPowder = new Product(2L, "Coriander Powder", "corianderPowder.png", "/admin/product/download/Coriander Powder", spices);
//        Product muttonMasala = new Product(3L, "Mutton Masala", "muttonMasala.png", "/admin/product/download/Mutton Masala", spices);
//        Product chickenMasala = new Product(4L, "Chicken Masala", "chickenMasala.png", "/admin/product/download/Chicken Masala", spices);
//        Product chilliPowder = new Product(5L, "Chilli Powder", "chilliPowder.png", "/admin/product/download/Chilli Powder", spices);
//
//        categoryRepo.saveAll(Arrays.asList(panMasala, spices));
//        logger.info("Categories added Successfully");
//        productRepo.saveAll(Arrays.asList(panMasalaPouch, corianderPowder, muttonMasala, chickenMasala, chilliPowder));
//        logger.info("Products added Successfully");
//    }
//
//    private void addDummyUserData() {
//
//        AdminProfile adminProfile1 = new AdminProfile(1L, "Suresh", "Balusu", "8765432109");
//        AdminProfile adminProfile2 = new AdminProfile(2L, "Kiran", "Manthena", "7654321098");
//        logger.info("Admin profiles added Successfully");
//
//        adminProfileRepo.saveAll(Arrays.asList(adminProfile1, adminProfile2));
//        logger.info("Admin profiles added Successfully");
//
//        // Create dummy Distributor profiles
//        DistributorProfile distributorProfile1 = new DistributorProfile(1L, FN_1, LN_1, PHONE_1, true, false, PICNAME_1, DISTRIBUTOR_PICURL_1);
//        DistributorProfile distributorProfile2 = new DistributorProfile(2L, FN_2, LN_2, PHONE_2, false, false, PICNAME_2, DISTRIBUTOR_PICURL_2);
//        logger.info("Dummy distributor Profiles added to the database.");
//
//        distributorProfileRepo.saveAll(Arrays.asList(distributorProfile1, distributorProfile2)); // , distributorProfile3, distributorProfile4
//        logger.info("Distributor profiles added Successfully");
//
//
//        // Create dummy customer profiles
//        ExecutiveProfile executiveProfile1 = new ExecutiveProfile(1L, FN_3, LN_3, PHONE_3, true, false, PICNAME_3, EXECUTIVE_PICURL_1, distributorProfile1);
//        ExecutiveProfile executiveProfile2 = new ExecutiveProfile(2L, FN_4, LN_4, PHONE_4, false, false, PICNAME_4, EXECUTIVE_PICURL_2, distributorProfile2);
//        logger.info("Dummy Executive profiles added to the database.");
//
//        executiveProfileRepo.saveAll(Arrays.asList(executiveProfile1, executiveProfile2)); // , executiveProfile3, executiveProfile4
//        logger.info("Executive profiles added Successfully");
//
//        // Create dummy users associated with staff profiles
//        User user1 = new User(1L, "8765432109", encoder.encode(pin), ERole.ADMIN, adminProfile1, null, null);
//        User user2 = new User(2L, "7654321098", encoder.encode(pin), ERole.ADMIN, adminProfile2, null, null);
//        User user3 = new User(3L, PHONE_1, encoder.encode(pin), ERole.DISTRIBUTOR, null, distributorProfile1, null);
//        User user4 = new User(4L, PHONE_2, encoder.encode(pin), ERole.DISTRIBUTOR, null, distributorProfile2, null);
//        User user5 = new User(5L, PHONE_3, encoder.encode(pin), ERole.EXECUTIVE, null, null, executiveProfile1);
//        User user6 = new User(6L, PHONE_4, encoder.encode(pin), ERole.EXECUTIVE, null, null, executiveProfile2);
//        logger.info("Dummy Users added to the database.");
//
//        userRepository.saveAll(Arrays.asList(user1, user2, user3, user4, user5, user6));
//        logger.info("Users added Successfully");
//
//        // Distributor product quantity
//        List<DistributorProductQuantity> dPQty1 = new ArrayList<>();
//        List<DistributorProductQuantity> dPQty2 = new ArrayList<>();
//        for (Product product : productRepo.findAll()) {
//            DistributorProductQuantity dpq1 = new DistributorProductQuantity(distributorProfileRepo.findById(1L).get(), product, 50);
//            dPQty1.add(dpq1);
//            DistributorProductQuantity dpq2 = new DistributorProductQuantity(distributorProfile2, product, 50);
//            dPQty2.add(dpq2);
//        }
//        dPQtyRepo.saveAll(dPQty1);
//        dPQtyRepo.saveAll(dPQty2);
//        logger.info("Dummy DistributorProductQuantity added to the database.");
//
//        logger.info("Dummy data added to the database.");
//    }
//
//    public void addOutlets() {
//        Outlet outlet1 = new Outlet(1L, "Balaji Kirana Store", "6789012345", "#21, 5th cross, chandanagar, BHEL, Hyderabad.", "Balaji Kirana Store.jpeg", "/admin/outlet/download/Balaji Kirana Store");
//        Outlet outlet2 = new Outlet(2L, "Deepak Kirana Store", "7679012342", "#41, 3th cross, Hi-tech city, Hyderabad.", "Deepak Kirana Store.jpeg", "/admin/outlet/download/Deepak Kirana Store");
//
//        outletRepo.saveAll(Arrays.asList(outlet1, outlet2));
//        logger.info("Outlets added Successfully");
//    }
//
//}
//
