def reset_models_for_acta():
    import gc
    from db.model_integrity_state import reset_integrity_failure

    reset_integrity_failure()

    # Detector RF-DETR (PyTorch)
    try:
        import models.detectormodel.new_evaluate_image as det
        det.__RFDETR_INSTANCE = None
    except Exception:
        pass

    # MNIST CapsNet
    try:
        import models.mnistmodel.model as mnist
        mnist.__MEMOIZED_MODEL = None
    except Exception:
        pass

    # Multi Spinal y MobileNet
    try:
        import models.binarymodel.valid_trazo_classification as bincls
        bincls.__MULTICLASS_CLASSIFIER_INSTANCE = None
    except Exception:
        pass

    # TensorFlow cleanup
    try:
        import tensorflow as tf
        tf.keras.backend.clear_session()
    except Exception:
        pass

    gc.collect()
