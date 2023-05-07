package net.alainpham;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import com.martensigwart.fakeload.FakeLoad;
import com.martensigwart.fakeload.FakeLoadExecutor;
import com.martensigwart.fakeload.FakeLoadExecutors;
import com.martensigwart.fakeload.FakeLoads;
import com.martensigwart.fakeload.MemoryUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Named("processing")
public class Processing implements Processor{

    
    Random generator = new Random();


    @ConfigProperty(name = "app.processing.exectimeavg.ms")
    double execTimeAvgMs;

    @ConfigProperty(name = "app.processing.exectimestdev.ms")
    double execTimeStdevMs;

    @ConfigProperty(name = "app.processing.failurerate.percentage")
    int failureRatePercentage;

    @ConfigProperty(name = "app.processing.cpuusage.percentage")
    int cpuUsagePercentage;
    @ConfigProperty(name = "app.processing.memusage.mb")
    int memUsageMB;


    @Override
    public void process(Exchange exchange) throws Exception {
        long randomExecTimeMs = Double.valueOf(Math.round(generator.nextGaussian()*execTimeStdevMs+execTimeAvgMs)).longValue() ;
        
        exchange.getIn().setBody(0);

        if (randomExecTimeMs>0){
            
            FakeLoad fakeload = FakeLoads.create()
            .lasting(randomExecTimeMs, TimeUnit.MILLISECONDS)
            .withCpu(cpuUsagePercentage)
            .withMemory(memUsageMB, MemoryUnit.MB);

            FakeLoadExecutor executor = FakeLoadExecutors.newDefaultExecutor();
            executor.execute(fakeload);
            exchange.getIn().setBody(randomExecTimeMs);

        }

        int failNb = generator.nextInt(100);

        if (failNb < failureRatePercentage){
            throw new Exception("The process has failed, something went very wrong!");
        }

    };

}
