public class Test {
    public static void main(String[] args) {
        String test = "1l";
        System.out.println(Integer.parseInt(test));
    }


    /*public static void main(String[] args) {
        // Установите путь к драйверу Chrome
        System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
        System.setProperty("webdriver.chrome.bin", "libs/chrome.exe");

        // Инициализируем WebDriver
        WebDriver driver = new ChromeDriver();

        try {
            String url = "https://anidub.live";
            driver.get(url);

            // Находим все элементы с классом .owl-item
            By owlItemSelector = By.cssSelector(".owl-item");
            java.util.List<WebElement> owlItems = driver.findElements(owlItemSelector);

            for (WebElement owlItem : owlItems) {
                // Здесь вы можете обрабатывать каждый элемент .owl-item по вашим потребностям
                System.out.println(owlItem.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }*/

}
