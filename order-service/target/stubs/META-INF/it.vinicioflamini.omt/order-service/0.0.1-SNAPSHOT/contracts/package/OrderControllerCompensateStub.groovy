import org.springframework.cloud.contract.spec.Contract
Contract compensate = Contract.make {
	priority 1
    description "should return message when compensate"
    request {
        method POST()
        url '/compensate' 
        headers {
			contentType(applicationJson())
		}
		body (
        	orderId: -1,
        	itemId: -1
		)
    }
    response {
        status 200
        body (
        	message: producer(regex(nonEmpty())),
        	orderId: -1
        )
    }
}