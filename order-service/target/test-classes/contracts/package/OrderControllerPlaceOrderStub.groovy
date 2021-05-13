import org.springframework.cloud.contract.spec.Contract
Contract placeOrder = Contract.make {
	priority 1
    description "should return message when placeOrder"
    request {
        method POST()
        url '/' 
        headers {
			contentType(applicationJson())
		}
		body (
        	itemId: -1,
        	customerId: -1
		)
    }
    response {
        status 200
        body (
        	message: producer(regex(nonEmpty())),
        	orderId: producer(regex(number())),
        	itemId: -1,
        	customerId: -1
        )
    }
}
