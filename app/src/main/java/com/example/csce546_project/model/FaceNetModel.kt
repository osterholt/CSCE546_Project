package com.example.csce546_project.model

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil

class FaceNetModel(context: Context,
				   var model : ModelInfo,
				   useGpu: Boolean) {

	val embeddingDim = model.outputDims

	private var interpreter: Interpreter

	init {
		val interpreterOptions = Interpreter.Options().apply {
			if(useGpu) {
				if(CompatibilityList().isDelegateSupportedOnThisDevice)
					addDelegate( GpuDelegate( CompatibilityList().bestOptionsForThisDevice ))
			} else {
				// TODO: Implement threads
			}
		}
		interpreter = Interpreter(FileUtil.loadMappedFile(context, model.assetsFilename), interpreterOptions)
	}

}