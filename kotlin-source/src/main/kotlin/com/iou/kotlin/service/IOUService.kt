package com.iou.kotlin.service

import com.iou.kotlin.flow.IOUCreation.IOUCreationFlow
import com.iou.kotlin.flow.IOUSettle.IOUSettleFlow
import com.iou.kotlin.flow.IOUTransfer.IOUTransferSubflow
import net.corda.core.node.PluginServiceHub

/**
 * This service registers a flow factory that is used when a initiating party attempts to communicate with us
 * using a particular flow. Registration is done against a marker class (in this case, [IOUFlow.Initiator])
 * which is sent in the session handshake by the other party. If this marker class has been registered then the
 * corresponding factory will be used to create the flow which will communicate with the other side. If there is no
 * mapping, then the session attempt is rejected.
 *
 * In short, this bit of code is required for the recipient in this Example scenario to respond to the sender using the
 * [IOUFlow.Acceptor] flow.
*/
object IOUService {
    class Service(services: PluginServiceHub) {
        init {
            services.registerFlowInitiator(IOUCreationFlow.Initiator::class) { IOUCreationFlow.Acceptor(it) }
            services.registerFlowInitiator(IOUSettleFlow.Initiator::class) { IOUSettleFlow.Acceptor(it) }
            services.registerFlowInitiator(IOUTransferSubflow.Initiator::class) { IOUTransferSubflow.Acceptor(it) }
        }
    }
}