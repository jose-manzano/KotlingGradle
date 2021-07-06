import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import java.time.Duration
import java.util.ArrayList
import java.util.Collections.synchronizedList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors.toCollection
import kotlin.math.abs
import com.google.common.util.concurrent.RateLimiter
import java.time.LocalDateTime


fun main() {

//	val capacity = 1L//5000L/1000
//	//val refill = Refill.greedy(capacity, Duration.ofMinutes(1))
//	//val limit = Bandwidth.classic(capacity, refill)
//	val limit = Bandwidth.simple(capacity, Duration.ofMinutes(1))
//	val bucket = Bucket4j.builder().addLimit(limit).build()

	val rateLimiter = RateLimiter.create(500.0/60.0)
	rateLimiter.rate = 12.0
	var consumerList = generateSequence { 1 }.take(500).toList()
	while (1 in consumerList) {
		println("Pending consumers $consumerList")
		println("Reminder consumers: ${consumerList.filter { it == 1 }.size}")
		val attemptTime = AtomicLong(1)
		consumerList = consumerList.parallelStream().map {
			//val acquire = rateLimiter.acquire()
			if (it != -1 && rateLimiter.tryAcquire(Duration.ofMillis(50))) {
				blockingOperation2()
				-1
			} else {
//				attemptTime.set(
//					TimeUnit.SECONDS.toMillis(acquire.toLong())
//				)
				if (it != -1) println(
					"no available tokens please come back later: ${attemptTime.get() / 1000F} seconds"
				)
				it
			}
		}.collect(toCollection { synchronizedList(ArrayList()) })
		//println("Waiting ${attemptTime.get() / 1000F} seconds to get token")
		//Thread.sleep(attemptTime.get())
	}
}

fun blockingOperation2() {
	//val delay = generateSequence(100L) { it + (1..100).random() }.take(50).toList().random()
	println("Start at:${LocalDateTime.now()}")
	val delay = (10L..50).random()
	Thread.sleep(delay)
	println("Done! elapsed: $delay ms")
}