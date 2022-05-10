package co.com.claro.sms.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.claro.sms.entity.Log;
import co.com.claro.sms.repository.LogRepository;

@Service
public class LogService {
	
	@Autowired
	private LogRepository logRepository;
	
	public Log insert(Log log) {
		
		return logRepository.insert(log);
		
	}
	
	public List<Log> findByDate(){
		
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
		
		Date startDate = calendar.getTime();
		
		calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR, 23);
		
		Date endDate = calendar.getTime();
		
		
		return logRepository.filterByDate(startDate, endDate);
		
		
	}

}
