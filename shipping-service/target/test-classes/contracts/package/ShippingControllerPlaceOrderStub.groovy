import org.springframework.cloud.contract.spec.Contract
Contract doInventory = Contract.make {
	priority 1
    description "should return message when placeOrder"
    request {
        method POST()
        url '/' 
        headers {
			contentType(applicationJson())
		}
		body (
			orderId: -1,
        	itemId: -1,
        	customerId: -1
		)
    }
    response {
        status 200
        body (
        	producer(regex(nonEmpty()))
        )
    }
}
