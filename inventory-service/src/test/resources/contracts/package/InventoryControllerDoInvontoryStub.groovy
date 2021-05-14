import org.springframework.cloud.contract.spec.Contract
Contract doInventory = Contract.make {
	priority 1
    description "should return message when doInventory"
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
        	message: producer(regex(nonEmpty())),
        	orderId: -1,
        	itemId: -1,
        	customerId: -1
        )
    }
}
