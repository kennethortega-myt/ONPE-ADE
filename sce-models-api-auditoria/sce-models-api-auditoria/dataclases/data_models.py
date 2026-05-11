from dataclasses import dataclass
from typing import Any, Optional

@dataclass
class SectionStaeVdParams:
    rectangle: Any
    name: str
    rec_id: str
    codigo_eleccion: str
    need_rotate: bool
    acta_type: str
    acta_id: int
    eleccion_id: int
    centro_computo: str = ""
    cod_usuario: str = ""

@dataclass
class SectionParams:
    rectangle: Any
    name: str
    rec_id: str
    codigo_eleccion: str
    copia_a_color: bool
    need_rotate: bool
    rows: int
    acta_type: str
    acta_id: int
    eleccion_id: int
    img_limpia_path: Optional[str] = None
    point0: Optional[Any] = None
    point1: Optional[Any] = None
    point2: Optional[Any] = None
    point3: Optional[Any] = None
    guide_lines: Optional[Any] = None
    acta_observada: bool = False
    centro_computo: str = ""
    cod_usuario: str = ""

@dataclass
class ProcessContext:
    acta_id: int
    eleccion_id: int
    acta_type: str
    codigo_eleccion: str
    file1_path: str
    config_map: dict
    need_rotate: bool
    copia_a_color: bool
    acta_observada: bool
    is_convencional: bool
    centro_computo: str
    cod_usuario: str
    marcadores_cache: dict | None = None


@dataclass
class VotosSectionParams:
    rec_loader: Any
    name: str
    rows: int
    is_convencional: bool
    section: SectionParams


@dataclass
class VotosParams(SectionParams):
    rec_loader: Any = None
    columns: Optional[int] = None # solo preferencial
@dataclass
class VotesProcessingContext:
    centro_computo: str
    cod_usuario: str
    rows: int
    codigo_eleccion: int
    is_coordenadas: bool
    copia_a_color: bool
    pipe_implemented: bool
    is_convencional: bool

@dataclass
class TotalVotesContext(VotesProcessingContext):
    vote_columns: list
    total_rows: int

@dataclass
class PrefVotesContext(VotesProcessingContext):
    columns: int

@dataclass
class PrefRowProcessingContext:
    pfctx: PrefVotesContext
    columns_count: int
    cell_bboxes_map: dict | None
    pipe_status_map: dict | None