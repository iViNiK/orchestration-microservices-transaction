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
        	itemId: -1
		)
    }
    response {
        status 200
        body (
        	"Request placed for item -1 fetching"
        )
    }
}
