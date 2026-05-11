package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.MiembrosMesaDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.repository.MiembroMesaEscrutinioRepository;
import pe.gob.onpe.scebackend.model.service.ConsultaMiembroMesaService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class ConsultaMiembroMesaServiceImpl implements ConsultaMiembroMesaService {

        @Autowired
        private MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository;

        @Override
        @Transactional(value = "locationTransactionManager", readOnly = true)
        public GenericResponse consultarMiembrosMesa(Integer numeroMesa, String dni) {
                GenericResponse genericResponse = new GenericResponse();

                Optional<Integer> tipoEleccion = miembroMesaEscrutinioRepository.obtenerTipoEleccion();
                if (tipoEleccion.isEmpty()) {
                        genericResponse.setSuccess(Boolean.FALSE);
                        genericResponse.setMessage("No se encontró la elección principal");
                        return genericResponse;
                }

                List<Map<String, Object>> electionResult = miembroMesaEscrutinioRepository.obtenerActasContabilizadas(tipoEleccion.get());
                if (electionResult == null || electionResult.isEmpty()) {
                        genericResponse.setSuccess(Boolean.FALSE);
                        genericResponse.setMessage("No se encontró información electoral");
                        return genericResponse;
                }

                if (!validarActasContabilizadas(electionResult)) {
                        genericResponse.setSuccess(Boolean.FALSE);
                        genericResponse.setMessage("Las actas aún no están procesadas al 100%");
                        return genericResponse;
                }

                if (dni != null && dni.isEmpty()) { dni = null; }

                List<Object[]> miembros = miembroMesaEscrutinioRepository.findMiembrosMesa(numeroMesa, dni);
                if (miembros.isEmpty()) {
                        genericResponse.setSuccess(Boolean.TRUE);
                        genericResponse.setMessage("No se encontraron miembros");
                        return genericResponse;
                }

                List<MiembrosMesaDto> miembrosDTO = miembros.stream()
                        .map(r -> new MiembrosMesaDto(
                                (String) r[0],
                                (String) r[1],
                                ((Number) r[2]).longValue(),
                                (String) r[3],
                                (String) r[4],
                                (String) r[5],
                                ((Number) r[6]).longValue(),
                                ((Number) r[7]).longValue()))
                                .toList();

                genericResponse.setSuccess(Boolean.TRUE);
                genericResponse.setMessage("Datos obtenidos correctamente");
                genericResponse.setData(miembrosDTO);

                return genericResponse;

        }

        private boolean validarActasContabilizadas(List<Map<String, Object>> electionResult) {
                return electionResult.stream().allMatch(f -> {
                        Object porcentaje = f.get("n_porcentaje_actas_contabilizadas");
                        return porcentaje instanceof Number number && number.doubleValue() == ConstantesComunes.PORCENTAJE_COMPLETO_ACTAS_CONTABILIZADAS;
                });
        }

        @Override
        @Transactional(value = "locationTransactionManager", readOnly = true)
        public GenericResponse getUbigeoByAcronimo(String acronimo) {
                GenericResponse genericResponse = new GenericResponse();
                List<Integer> ubigeos = miembroMesaEscrutinioRepository.findUbigeoByAcronimo(acronimo);

                if (ubigeos != null && !ubigeos.isEmpty()) {
                        genericResponse.setSuccess(Boolean.TRUE);
                        genericResponse.setMessage("Datos obtenidos correctamente");
                        genericResponse.setData(ubigeos);
                } else {
                        genericResponse.setSuccess(Boolean.FALSE);
                        genericResponse.setMessage("No se encontraron ubigeos para el acrónimo proporcionado");
                }

                return genericResponse;
        }
}
