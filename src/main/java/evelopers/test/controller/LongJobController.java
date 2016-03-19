package evelopers.test.controller;

import evelopers.test.NamesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import evelopers.test.controller.exception.ExceptionJSONInfo;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class LongJobController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Month, Future<List<String>>> tasks  = new EnumMap<>(Month.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Autowired
    private NamesLoader loader;


    @RequestMapping(value = "/submit_job", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus( HttpStatus.OK )
    public QueNum submitJob(@RequestParam(value = "month", required = false) Optional<Integer> month) throws InterruptedException {
        logger.info("submit_job for month " + month);

        Month m = month.isPresent() ? Month.of(month.get()) : LocalDate.now().getMonth();
        if(tasks.get(m) == null) {
            Callable<List<String>> callable = new LongTask(m);
            Future<List<String>> future = executor.submit(callable);
            tasks.put(m, future);
            logger.info("Servlet thread released");
        }
        return new QueNum(m);
    }


    @RequestMapping(value = "/get_result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus( HttpStatus.OK )
    public String getResult(@RequestParam(value = "month", required = false) Optional<Integer> month) throws Exception{
        logger.info("get_result for " + month);
        Month m = month.isPresent() ? Month.of(month.get()) : LocalDate.now().getMonth();
        Future<List<String>> f = tasks.get(m);
        if(f == null)
            return "submit job first";
        if(f.isDone())
            return f.get().toString();
        return "Not ready";
    }



    class QueNum {
        private final Month queNum;

        private QueNum(Month number) {
            this.queNum = number;
        }

        public Integer getQueNum() {
            return queNum.getValue();
        }
    }


    private class LongTask implements Callable<List<String>> {
        private Month month;

        public LongTask(Month m) throws InterruptedException {
            this.month = m;
        }

        @Override
        public List<String> call() throws Exception {
            logger.info("Started for " + month);
            Thread.sleep(1000 * 60);
            logger.info("Finished for " + month);
            return loader.getNames(month);
        }
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ExceptionJSONInfo handleEmployeeNotFoundException(HttpServletRequest request, Exception ex){
        ExceptionJSONInfo response = new ExceptionJSONInfo();
        response.setUrl(request.getRequestURL().toString());
        response.setMessage(ex.getMessage() == null ? "error on making request" : ex.getMessage());
        return response;
    }

}


