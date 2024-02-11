import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.sql.DataSource;

// Entity для пользователя
@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // геттеры и сеттеры...
}

// Репозиторий для работы с пользователями
@Repository
interface UserRepository extends JpaRepository<User, Long> {
}

// Сервис для управления операциями с пользователями
@Service
class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}

// Аспект для логирования
@Aspect
@Component
class LoggingAspect {
    private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Calling method: " + methodName);
    }
}

// Конфигурация транзакций
@Configuration
@EnableTransactionManagement
class TransactionConfig {
    @Bean
    public DataSource dataSource() {
        // Настройка источника данных (DataSource)
        return null;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}

// Основная конфигурация Spring
@Configuration
@ComponentScan
class AppConfig {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = context.getBean(UserService.class);

        // Пример использования сервиса
        User user = new User();
        user.setName("John Doe");
        userService.addUser(user);

        User retrievedUser = userService.getUserById(1);
        System.out.println("Retrieved user: " + retrievedUser.getName());

        context.close();
    }
}
