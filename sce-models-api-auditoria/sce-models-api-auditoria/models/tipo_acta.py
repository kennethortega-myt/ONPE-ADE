from util import constantes

# Diccionario de configuraciones por tipo de elección
TIPO_ELECCION_CONFIG = {
    constantes.COD_ELEC_PRESIDENTE: {"personero_need_rotate": False, "personero_stae_need_rotate": True},
    constantes.COD_ELEC_CONGRESAL: {"personero_need_rotate": True, "personero_stae_need_rotate": False},
    constantes.COD_ELEC_PARLAMENTO: {"personero_need_rotate": False, "personero_stae_need_rotate": True},
    constantes.COD_ELEC_DIPUTADO: {"personero_need_rotate": False, "personero_stae_need_rotate": True},
    constantes.COD_ELEC_SENADO_MULTIPLE: {"personero_need_rotate": False, "personero_stae_need_rotate": True}, # vertical
    constantes.COD_ELEC_SENADO_UNICO: {"personero_need_rotate": False, "personero_stae_need_rotate": True}, # horizontal
    constantes.COD_ELEC_REGIONAL: {"personero_need_rotate": False, "personero_stae_need_rotate": False},
    constantes.COD_ELEC_CONSEJERO: {"personero_need_rotate": False, "personero_stae_need_rotate": False},
    constantes.COD_ELEC_DISTRITAL: {"personero_need_rotate": True, "personero_stae_need_rotate": False},
    constantes.COD_ELEC_PROVINCIAL: {"personero_need_rotate": False, "personero_stae_need_rotate": False},
    constantes.COD_ELEC_REVOCATORIA: {"personero_need_rotate": False, "personero_stae_need_rotate": False},
}

CONFIGURACION_ELECCION_STAE_VD = {
    constantes.COD_ELEC_PRESIDENTE: {
        "totales_1":{"cols": [2, 3], "rows": [1, -2]},
        "totales_2": {"cols": [2, 3], "rows": [1, -2]}
        },
    constantes.COD_ELEC_PARLAMENTO: {
        "totales_1": {"cols": [2, 3], "rows": [1, -2]},
        "totales_2": {"cols": [2, 3], "rows": [1, -2]},
        "preferenciales_1": {"cols": [3, 4], "rows": [1, -2]},
        "preferenciales_2": {"cols": [3, 4], "rows": [1, -3]}
    },
    constantes.COD_ELEC_DIPUTADO: {
        "totales_1": {"cols": [2, 3], "rows": [1, -2]},
        "totales_2": {"cols": [2, 3], "rows": [1, -2]},
        "preferenciales_1": {"cols": [3, 4], "rows": [1, -2]},
        "preferenciales_2": {"cols": [3, 4], "rows": [1, -3]}
    },
    constantes.COD_ELEC_SENADO_UNICO: {
        "totales_1": {"cols": [2, 3], "rows": [1, -2]},
        "totales_2": {"cols": [2, 3], "rows": [1, -2]},
        "preferenciales_1": {"cols": [3, 4], "rows": [1, -2]},
        "preferenciales_2": {"cols": [3, 4], "rows": [1, -3]}
    },
    constantes.COD_ELEC_SENADO_MULTIPLE: {
        "totales_1": {"cols": [2, 3], "rows": [1, -2]},
        "totales_2": {"cols": [2, 3], "rows": [1, -2]},
        "preferenciales_1": {"cols": [3, 4], "rows": [1, -2]},
        "preferenciales_2": {"cols": [3, 4], "rows": [1, -3]}
    }
}

CONFIGURACION_ELECCION = {
    constantes.COD_ELEC_PRESIDENTE: {
        "cols": [2, 3], "rows": [1, -2], "guia_filas": [2, -3]
        },
    constantes.COD_ELEC_REVOCATORIA: {
        "cols": [2, 3], "rows": [1, 2], "guia_filas": []
        },
    constantes.COD_ELEC_PARLAMENTO: {
        "totales": {"cols": [2, 3], "rows": [1, -2], "guia_filas": [2, -3]},
        "preferenciales": {"cols": [3, 4], "rows": [1, -3], "guia_filas": [2, -3]},
    },
    constantes.COD_ELEC_DIPUTADO: {
        "totales": {"cols": [2, 3], "rows": [1, -2], "guia_filas": [2, -3]},
        "preferenciales": {"cols": [3, 4], "rows": [1, -3], "guia_filas": [2, -3]},
    },
    constantes.COD_ELEC_SENADO_UNICO: {
        "totales": {"cols": [2, 3], "rows": [1, -2], "guia_filas": [2, -3]},
        "preferenciales": {"cols": [3, 4], "rows": [1, -3], "guia_filas": [2, -3]},
    },
    constantes.COD_ELEC_SENADO_MULTIPLE: {
        "totales": {"cols": [2, 3], "rows": [1, -2], "guia_filas": [2, -3]},
        "preferenciales": {"cols": [3, 4], "rows": [1, -3], "guia_filas": [2, -3]},
    },
    constantes.COD_ELEC_DISTRITAL: {
        "cols": [2, 3], "rows": [1, 3], "guia_filas": []
    },
}

CODIGOS_TIPO_ELECCIONES = set(TIPO_ELECCION_CONFIG.keys())

# Plantillas base
TEMPLATE_5x5 = [
    [1, 1, 1, 1, 1],
    [1, 0, 0, 0, 1],
    [1, 0, 0, 0, 1],
    [1, 0, 0, 0, 1],
    [1, 0, 0, 0, 1]
]

TEMPLATE_6x5 = [
    [1, 1, 1, 1, 1, 1],
    [1, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1]
]

TEMPLATE_6x4 = [
    [1, 1, 1, 1, 1, 1],
    [1, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1]
]

# Diccionario de mapping
TEMPLATE_MAP = {
    constantes.COD_ELEC_PRESIDENTE: {"matrix": TEMPLATE_5x5, "expand": True},
    constantes.COD_ELEC_CONGRESAL: {"matrix": [1, 1, 1, 1, 1, 1], "expand": False},
    constantes.COD_ELEC_PARLAMENTO: {"matrix": TEMPLATE_6x5, "expand": True},
    constantes.COD_ELEC_DIPUTADO: {"matrix": TEMPLATE_6x5, "expand": True},
    constantes.COD_ELEC_SENADO_MULTIPLE: {"matrix": TEMPLATE_6x5, "expand": True},
    constantes.COD_ELEC_SENADO_UNICO: {"matrix": TEMPLATE_6x4, "expand": True},
    constantes.COD_ELEC_REGIONAL: {"matrix": [1, 1, 1, 1, 1, 1], "expand": False},
    constantes.COD_ELEC_CONSEJERO: {"matrix": [1, 1, 1, 1, 1, 1], "expand": False},
    constantes.COD_ELEC_DISTRITAL: {"matrix": TEMPLATE_5x5, "expand": True},
    constantes.COD_ELEC_PROVINCIAL: {"matrix": [1, 1, 1, 1, 1, 1], "expand": False},
    constantes.COD_ELEC_REVOCATORIA: {"matrix": TEMPLATE_6x4, "expand": False},
}


class TipoActa:
    def __init__(self, tipo_eleccion: str, tipo_acta: int = None,
                 cantidad_candidatos: int = None, acta_tipo_disenio: str = None):
        if tipo_eleccion not in CODIGOS_TIPO_ELECCIONES:
            raise ValueError(f"Error: Tipo de Elección no reconocido: {tipo_eleccion}")
        if tipo_acta is not None and tipo_acta not in [2, 3]:
            raise ValueError(f"Error: Tipo de Acta no reconocido: {tipo_acta}")

        self._codigo_tipo_eleccion = tipo_eleccion
        self._codigo_tipo_acta = tipo_acta
        self._acta_tipo_disenio = acta_tipo_disenio

        candidatos = cantidad_candidatos if cantidad_candidatos is not None else 0
        self._cantidad_candidatos = candidatos - 4 if candidatos > 4 else candidatos

    def get_personero_rotation_flag(self):
        return TIPO_ELECCION_CONFIG[self._codigo_tipo_eleccion]["personero_need_rotate"]

    def get_personero_stae_rotation_flag(self):
        return TIPO_ELECCION_CONFIG[self._codigo_tipo_eleccion]["personero_stae_need_rotate"]

    def get_es_horizontal(self):
        return self._acta_tipo_disenio in [constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO]

    def get_template_marcadores(self, con_guias: bool = False):
        """
        Devuelve el template de marcadores en base al tipo de elección,
        y adicionalmente ajusta el caso especial de DIPUTADO con
        acta_tipo_disenio en {AEH, AESH}.
        """
        # Caso especial: DIPUTADO con AEH/AESH
        if (self._codigo_tipo_eleccion == constantes.COD_ELEC_DIPUTADO and 
            self._acta_tipo_disenio in (constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO)):
            base = TEMPLATE_6x4
            expand = True
        else:
            config = TEMPLATE_MAP[self._codigo_tipo_eleccion]
            base = config["matrix"]
            expand = config["expand"]

        # Si es lista plana (no matriz), devolver directo
        if isinstance(base[0], int):
            return base

        # Expandir matriz si corresponde
        return self._expandir_matriz(base) if (con_guias and expand) else base

    def _expandir_matriz(self, matriz):
        cantidad = self._cantidad_candidatos // 20
        if cantidad == 0:
            return matriz
        fila_a_repetir = matriz[1]
        nueva_matriz = matriz[:1] + [fila_a_repetir] * cantidad + matriz[1:]
        return nueva_matriz
