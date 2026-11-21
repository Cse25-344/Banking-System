package banking;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface InterestBearing {
    BigDecimal calculateMonthlyInterest();
    void payMonthlyInterest(YearMonth month);
}
