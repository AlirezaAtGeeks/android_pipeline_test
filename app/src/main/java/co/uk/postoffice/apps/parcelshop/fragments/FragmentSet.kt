package co.uk.postoffice.apps.parcelshop.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.uk.postoffice.apps.parcelshop.R
import org.slf4j.Logger


class FragmentSet:Fragment(){


    companion object {

        lateinit var LOGGER:Logger

        fun getInstance(logger: Logger):FragmentSet{
            LOGGER = logger
            return FragmentSet()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))

        return  inflater.inflate(R.layout.fragment_set, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStart() {
        super.onStart()
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onResume() {
        super.onResume()
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onDetach() {
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStop() {
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
        super.onStop()
    }

    override fun onDestroy() {
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
        super.onDestroy()
    }

}