package luizsignorelli.prometheusdemo.order

import org.apache.commons.lang3.RandomUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OrderProcessor {

    val errorRate = 0
    val log = LoggerFactory.getLogger("OrdersProcessor")!!

    @Async("orderProcessorExecutor")
    fun process(order: Order) {
        OrderMetrics.processingTime( Runnable {
            try {
                log.info("Started order {} processing.", order.id)
                OrderRepository.processing(order.id)
                doProcess(order)
                OrderRepository.complete(order.id)
                log.info("Finished order {} processing.", order.id)
            } catch (e: Exception) {
                OrderRepository.fail(order.id)
                throw e
            }
        })
    }

    private fun doProcess(order: Order) {
        if (RandomUtils.nextLong(1, 11) < errorRate) {
            throw RuntimeException("order ${order.id} processing failed.")
        }
        TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 2000))
    }
}