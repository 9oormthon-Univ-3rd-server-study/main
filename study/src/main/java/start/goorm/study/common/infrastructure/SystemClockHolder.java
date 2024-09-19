package start.goorm.study.common.infrastructure;

import org.springframework.stereotype.Component;
import start.goorm.study.common.domain.ClockHolder;

import java.time.Clock;

@Component
public class SystemClockHolder implements ClockHolder {

    @Override
    public long mills() {
        return Clock.systemUTC().millis();
    }


}
