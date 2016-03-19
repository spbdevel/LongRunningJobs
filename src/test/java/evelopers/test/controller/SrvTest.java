package evelopers.test.controller;

import evelopers.test.AppConfig;
import evelopers.test.NamesLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AppConfig.class} )
@WebAppConfiguration
public class SrvTest {


    @Autowired
    private NamesLoader loader;

    @Autowired
    private LongJobController longJobController;

    @Test
    public void testLoader() {
        List names = loader.getNames(Month.JANUARY);
        assertEquals(1, names.size());
    }

    @Test
    public void testSubmit() throws Exception {
        LongJobController.QueNum queNum = longJobController.submitJob(Optional.of(1));
        assertEquals(1, queNum.getQueNum().intValue());
    }
}
