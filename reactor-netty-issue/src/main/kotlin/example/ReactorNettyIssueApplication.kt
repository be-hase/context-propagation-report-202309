package example

import example.ReactorNettyIssueApplication.MyThreadLocalAccessor
import io.micrometer.context.ContextRegistry
import io.micrometer.context.ThreadLocalAccessor
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import java.util.*


fun main(args: Array<String>) {
    ContextRegistry.getInstance().registerThreadLocalAccessor(MyThreadLocalAccessor())
    Hooks.enableAutomaticContextPropagation()
    runApplication<ReactorNettyIssueApplication>(*args)
}

@SpringBootApplication
class ReactorNettyIssueApplication {

    @RestController
    class Controller(
        webClientBuilder: WebClient.Builder
    ) {
        private val log = LoggerFactory.getLogger(javaClass)
        private val webClient: WebClient = webClientBuilder.filter(LogFilterFunction()).build()

        @GetMapping("/")
        fun test(): String {
            myThreadLocal.set(UUID.randomUUID().toString())
            log.info("Controller. MDC={}, myThreadLocal={}", MDC.getCopyOfContextMap(), myThreadLocal.get())

            webClient.get().uri("https://example.com").retrieve().toBodilessEntity()
                .doOnSuccess {
                    log.info("doOnSuccess. MDC={}, myThreadLocal={}", MDC.getCopyOfContextMap(), myThreadLocal.get())
                }
                .block()
            return "OK"
        }
    }

    class LogFilterFunction : ExchangeFilterFunction {
        private val log = LoggerFactory.getLogger(javaClass)

        override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
            log.info("Request. MDC={}, myThreadLocal={}", MDC.getCopyOfContextMap(), myThreadLocal.get())
            return next.exchange(request)
                .doOnSuccess {
                    log.info("Response. MDC={}, myThreadLocal={}", MDC.getCopyOfContextMap(), myThreadLocal.get())
                }
        }
    }


    class MyThreadLocalAccessor : ThreadLocalAccessor<String> {
        override fun key(): Any {
            return KEY
        }

        override fun getValue(): String? {
            return myThreadLocal.get()
        }

        override fun setValue(value: String) {
            myThreadLocal.set(value)
        }

        override fun setValue() {
            myThreadLocal.remove()
        }

        companion object {
            val KEY = MyThreadLocalAccessor::class.java
        }
    }

    companion object {
        val myThreadLocal = ThreadLocal<String>()
    }
}
