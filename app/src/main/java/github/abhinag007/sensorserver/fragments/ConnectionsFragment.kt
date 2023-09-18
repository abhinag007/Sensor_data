package github.abhinag007.sensorserver.fragments

import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import github.abhinag007.sensorserver.databinding.FragmentConnectionsBinding
import github.abhinag007.sensorserver.fragments.customadapters.ConnectionsRecyclerViewAdapter
import github.abhinag007.sensorserver.service.SensorService
import github.abhinag007.sensorserver.service.SensorService.LocalBinder
import github.abhinag007.sensorserver.service.ServiceBindHelper
import github.abhinag007.sensorserver.util.UIUtil
import org.java_websocket.WebSocket

/**
 * TODO: functionality to allow user to close all connections (using button in action bar)
 */
class ConnectionsFragment : Fragment()
{

    private var sensorService: SensorService? = null
    private lateinit var serviceBindHelper: ServiceBindHelper

    private lateinit var connectionsRecyclerViewAdapter: ConnectionsRecyclerViewAdapter
    private val webSockets = ArrayList<WebSocket>()


    private var _binding : FragmentConnectionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        Log.d(TAG, "onCreateView()")

        _binding = FragmentConnectionsBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setLayoutManager(LinearLayoutManager(context))

        connectionsRecyclerViewAdapter = ConnectionsRecyclerViewAdapter(webSockets)
        binding.recyclerView.setAdapter(connectionsRecyclerViewAdapter)


        serviceBindHelper = ServiceBindHelper(
            context = requireContext(),
            service = SensorService::class.java,
            componentLifecycle = lifecycle
        )

        serviceBindHelper.onServiceConnected(this::onServiceConnected)

    }

    override fun onPause()
    {
        super.onPause()
        Log.d(TAG, "onPause()")

        // To prevent memory leak
        sensorService?.onConnectionsChange( callBack = null)
    }


    fun onServiceConnected(binder: IBinder)
    {
        val localBinder = binder as LocalBinder
        sensorService = localBinder.service


        sensorService?.onConnectionsChange{ webSockets ->

            this.webSockets.clear()
            this.webSockets.addAll(webSockets)

            handleNoConnectionsText()
            UIUtil.runOnUiThread { connectionsRecyclerViewAdapter.notifyDataSetChanged() }
        }



        sensorService?.getConnectedClients().let { webSockets ->

            this.webSockets.clear()
            webSockets?.let{this.webSockets.addAll(it)}

            connectionsRecyclerViewAdapter.notifyDataSetChanged()

        }

        handleNoConnectionsText()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun handleNoConnectionsText()
    {
        if (webSockets.size > 0) UIUtil.runOnUiThread {
            binding.noConnectionsText.visibility = View.INVISIBLE
        }
        else UIUtil.runOnUiThread { binding.noConnectionsText.visibility = View.VISIBLE }
    }

    companion object
    {
        private val TAG: String = ConnectionsFragment::class.java.getSimpleName()
    }
}