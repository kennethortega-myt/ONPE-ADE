package pe.gob.onpe.sceorcbackend.utils.trazabilidad;

import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;

public class ConstantesEstadosTrazabilidad {

  /*
  * Es la unión de los estados acta, computo y digitalización en ese orden*/
  private ConstantesEstadosTrazabilidad(){

  }

  public static final String ESTADOS_COMBINADOS_ACTA_RECIBIDA  = String.join("",
      ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE,
      ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE,
      ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA
  );


  public static final String ESTADOS_COMBINADOS_ACTA_RECHAZADA = String.join("",
      ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE,
      ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE,
      ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA
  );

  public static final String ESTADOS_COMBINADOS_ACTA_APROBADA  = String.join("",
      ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA,
      ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE,
      ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA
  );

  public static final String ESTADOS_COMBINADOS_ACTA_1ERA_VERIFICACION  = String.join("",
      ConstantesEstadoActa.ESTADO_ACTA_DIGITADA,
      ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO,
      ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA
  );


  public static final String ESTADOS_COMBINADOS_ACTA_2DA_VERIFICACION  = String.join("",
      ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION,
      ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO,
      ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA
  );


  public static final String ESTADOS_COMBINADOS_ACTAXCORREGIR  = String.join("",
          ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR,
          ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO,
          ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA
  );





}
