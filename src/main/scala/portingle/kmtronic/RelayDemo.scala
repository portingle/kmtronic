package portingle.kmtronic



/** this code assumes you have a KMTronic device hooked up as the virtual port com5: 

If you don't have the device plugged in then you'll get a runtime error relating to com5: not being found.
*/
object RelayDemo extends App {

	val relayInstance = 1
	val comPortNumber = 5
	val numberOfRelays = 2

	val relays = new Relay(new WindowsComPort(comPortNumber), numberOfRelays)
	relays.open()

	val relay : RelayInstance = relays(relayInstance)

	relay.powerOn() 
	assert( true == relay.isPowered, "expected relay to be on" )


	relay.powerOff() 
	assert( false == relay.isPowered, "expected relay to be off" )
}
