import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import * as XLSX from 'xlsx'; // Importar la librería XLSX
import logo from '../img/ESLOGAN CONI.png';
import '../App.css'; // Asegúrate de que tus estilos CSS estén aquí

const InformeModulo = () => {
    const navigate = useNavigate();

    // --- ESTADOS PARA LA INFORMACIÓN DEL USUARIO ---
    const [currentUser, setCurrentUser] = useState(null);
    const [cargandoUsuario, setCargandoUsuario] = useState(true);

    // --- ESTADOS PARA EL INFORME ACTUAL ---
    const [reportData, setReportData] = useState([]);
    const [cargandoReporte, setCargandoReporte] = useState(false);
    const [errorReporte, setErrorReporte] = useState('');
    const [filterStatus, setFilterStatus] = useState('all'); // Filtro de estado para el informe actual

    // --- ESTADOS PARA INFORMES HISTÓRICOS ---
    const [historicalReports, setHistoricalReports] = useState([]);
    const [cargandoHistorico, setCargandoHistorico] = useState(false);
    const [errorHistorico, setErrorHistorico] = useState('');
    const [selectedHistoricalReportData, setSelectedHistoricalReportData] = useState(null); // Para ver un informe histórico completo
    const [isHistoricalModalOpen, setIsHistoricalModalOpen] = useState(false);

    // Opciones de estado de asignación para el filtro (basadas en equipos_perifericos.estado)
    const assignmentStatusOptions = [
        { value: 'all', label: 'Todos los Estados' },
        { value: 'ASIGNADO', label: 'asignado' },
        { value: 'DISPONIBLE', label: 'Disponible' },
        { value: 'PENDIENTE', label: 'Pendiente' },
        // Puedes añadir más estados si los tienes en tu DB para equipos_perifericos.estado
    ];

    // headers de autenticación (CLAVE PARA iOS)
    
    const getAuthHeaders = () => {
        const storedUserJSON = localStorage.getItem("usuarioLogueado");
        const usuario = storedUserJSON ? JSON.parse(storedUserJSON) : null;

        return {
            'Content-Type': 'application/json',
            'X-User-Id': usuario?.idUsuario || usuario?.id || ''
        };
    };

    // VALIDAR USUARIO
    // ===============================
    useEffect(() => {
        const storedUserJSON = localStorage.getItem("usuarioLogueado");

        if (!storedUserJSON) {
            navigate("/");
            return;
        }

        try {
            const usuario = JSON.parse(storedUserJSON);
            const id = usuario.idUsuario || usuario.id;
            const rol = usuario.rolAutenticacion;
            const cargo = usuario.cargoEmpleado || localStorage.getItem("cargoEmpleado");

            if (!id || !rol || !cargo) {
                navigate("/");
                return;
            }

            if (rol !== 'admin') {
                navigate("/");
                return;
            }

            setCurrentUser({ id, rol, cargo });
        } catch {
            navigate("/");
        } finally {
            setCargandoUsuario(false);
        }
    }, [navigate]);

    // ===============================
    // INFORME ACTUAL
    // ===============================
    const fetchCurrentReport = useCallback(async () => {
        if (!currentUser?.id) return;

        setCargandoReporte(true);
        setErrorReporte('');

        try {
            const params = new URLSearchParams();
            if (filterStatus !== 'all') {
                params.append('filterStatus', filterStatus);
            }

            const response = await fetch(
                `https://coni-backend.onrender.com/informes/inventario?${params.toString()}`,
                {
                    credentials: 'include',
                    headers: getAuthHeaders()
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensaje || 'Error al cargar informe');
            }

            const data = await response.json();
            setReportData(data);
        } catch (err) {
            setErrorReporte(`No se pudo cargar el informe: ${err.message}`);
        } finally {
            setCargandoReporte(false);
        }
    }, [currentUser, filterStatus]);

    // ===============================
    // HISTÓRICOS (LISTA)
    // ===============================
    const fetchHistoricalReports = useCallback(async () => {
        if (!currentUser?.id) return;

        setCargandoHistorico(true);
        setErrorHistorico('');

        try {
            const response = await fetch(
                'https://coni-backend.onrender.com/informes/historico',
                {
                    credentials: 'include',
                    headers: getAuthHeaders()
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensaje || 'Error al cargar históricos');
            }

            const data = await response.json();
            setHistoricalReports(data);
        } catch (err) {
            setErrorHistorico(err.message);
        } finally {
            setCargandoHistorico(false);
        }
    }, [currentUser]);

    useEffect(() => {
        if (currentUser) {
            fetchCurrentReport();
            fetchHistoricalReports();
        }
    }, [currentUser, fetchCurrentReport, fetchHistoricalReports]);

    // ===============================
    // GUARDAR + DESCARGAR
    // ===============================
    const handleGenerateAndDownload = async () => {
        if (reportData.length === 0) {
            alert("No hay datos para generar el informe.");
            return;
        }

        try {
            const response = await fetch(
                'https://coni-backend.onrender.com/informes/guardar',
                {
                    method: 'POST',
                    credentials: 'include',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        estadoFiltro: filterStatus,
                        reporteJson: JSON.stringify(reportData)
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.mensaje);
            }

            fetchHistoricalReports();
        } catch (err) {
            alert(`Error al guardar informe: ${err.message}`);
        }

        exportToExcel(
            reportData,
            `Informe_Inventario_${filterStatus}_${new Date().toISOString().slice(0, 10)}`
        );
    };

    // ===============================
    // HISTÓRICO ESPECÍFICO
    // ===============================
    const fetchHistoricalDetail = async (id, download = false) => {
        try {
            const response = await fetch(
                `https://coni-backend.onrender.com/informes/historico?id=${id}`,
                {
                    credentials: 'include',
                    headers: getAuthHeaders()
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.mensaje);
            }

            const data = await response.json();

            if (download) {
                exportToExcel(data, `Informe_Historico_${id}`);
            } else {
                setSelectedHistoricalReportData(data);
                setIsHistoricalModalOpen(true);
            }
        } catch (err) {
            alert(err.message);
        }
    };

    // ===============================
    // EXCEL
    // ===============================
    const exportToExcel = (data, filename) => {
        const ws = XLSX.utils.json_to_sheet(data);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Informe");
        XLSX.writeFile(wb, `${filename}.xlsx`);
    };

    // ===============================
    // LOGOUT
    // ===============================
    const handleLogout = async () => {
        await fetch("https://coni-backend.onrender.com/LogoutServlet", {
            method: "GET",
            credentials: "include",
            headers: getAuthHeaders()
        });

        localStorage.clear();
        sessionStorage.clear();
        navigate("/");
    };

  return (
  <div className="informe-modulo">
    <header className="encabezado">
      <img src={logo} className="imagen-encabezado" alt="Logo CONI" />
      <div className="barra-superior">
        <nav>
          <ul>
            <li>
              <button onClick={handleLogout}>Cerrar sesión</button>
            </li>
          </ul>
        </nav>
      </div>
    </header>

    <main>
      <h2>Generar Informes</h2>

      {cargandoUsuario ? (
        <p>Cargando información del usuario...</p>
      ) : (
        <>
          <div className="filtros-container">
            <label htmlFor="filterStatus">Filtrar por Estado:</label>
            <select
              id="filterStatus"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
            >
              {assignmentStatusOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            <button onClick={fetchCurrentReport}>Actualizar Reporte</button>
          </div>

          <div className="seccion-informe">
            <h3>Informe de Inventario Actual</h3>

            {cargandoReporte && <p>Cargando reporte...</p>}
            {errorReporte && <p className="error-mensaje">{errorReporte}</p>}

            {!cargandoReporte && !errorReporte && reportData.length === 0 && (
              <p>
                No hay datos disponibles para el informe actual con los filtros
                seleccionados.
              </p>
            )}

            {!cargandoReporte && reportData.length > 0 && (
              <>
                <table className="tabla-reporte">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Categoría</th>
                      <th>Tipo</th>
                      <th>Marca</th>
                      <th>Serial</th>
                      <th>RAM</th>
                      <th>Disco</th>
                      <th>Procesador</th>
                      <th>Estado Asignación</th>
                      <th>Asignado A</th>
                      <th>Fecha Asignación</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.map((item) => (
                      <tr key={`${item.categoria}-${item.id}`}>
                        <td>{item.id}</td>
                        <td>{item.categoria}</td>
                        <td>{item.tipo}</td>
                        <td>{item.marca}</td>
                        <td>{item.serial}</td>
                        <td>{item.ram || "N/A"}</td>
                        <td>{item.disco || "N/A"}</td>
                        <td>{item.procesador || "N/A"}</td>
                        <td>{item.estadoAsignacion}</td>
                        <td>{item.asignadoA}</td>
                        <td>
                          {item.fechaAsignacion
                            ? new Date(item.fechaAsignacion).toLocaleString()
                            : "N/A"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <button
                  onClick={handleGenerateAndDownload}
                  className="btn-descargar"
                >
                  Generar y Descargar Excel
                </button>
              </>
            )}
          </div>

          <div className="seccion-historico">
            <h3>Informes Históricos Guardados</h3>

            {cargandoHistorico && <p>Cargando informes históricos...</p>}
            {errorHistorico && (
              <p className="error-mensaje">{errorHistorico}</p>
            )}

            {!cargandoHistorico && historicalReports.length === 0 && (
              <p>No hay informes históricos guardados.</p>
            )}

            {!cargandoHistorico && historicalReports.length > 0 && (
              <ul className="lista-historicos">
                {historicalReports.map((reporte) => (
                  <li key={reporte.id}>
                    <p>
                      <strong>ID:</strong> {reporte.id}
                    </p>
                    <p>
                      <strong>Fecha Guardado:</strong>{" "}
                      {new Date(reporte.fechaGeneracion).toLocaleString()}
                    </p>
                    <p>
                      <strong>Estado de Filtro:</strong>{" "}
                      {reporte.estadoFiltro}
                    </p>

                    <div className="acciones-historico">
                      <button
                        onClick={() => handleViewHistorical(reporte.id)}
                      >
                        Ver Reporte
                      </button>
                      <button
                        onClick={() =>
                          handleDownloadHistorical(
                            reporte.id,
                            `Informe_Historico_${reporte.id}`
                          )
                        }
                      >
                        Descargar Excel
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {isHistoricalModalOpen && selectedHistoricalReportData && (
            <div className="modal">
              <div className="modal-content">
                <h3>Detalles del Informe Histórico</h3>

                <div className="tabla-modal-wrapper">
                  <table className="tabla-modal">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Categoría</th>
                        <th>Tipo</th>
                        <th>Marca</th>
                        <th>Serial</th>
                        <th>RAM</th>
                        <th>Disco</th>
                        <th>Procesador</th>
                        <th>Estado Asignación</th>
                        <th>Asignado A</th>
                        <th>Fecha Asignación</th>
                      </tr>
                    </thead>
                    <tbody>
                      {selectedHistoricalReportData.map((item) => (
                        <tr key={`${item.categoria}-${item.id}`}>
                          <td>{item.id}</td>
                          <td>{item.categoria}</td>
                          <td>{item.tipo}</td>
                          <td>{item.marca}</td>
                          <td>{item.serial}</td>
                          <td>{item.ram || "N/A"}</td>
                          <td>{item.disco || "N/A"}</td>
                          <td>{item.procesador || "N/A"}</td>
                          <td>{item.estadoAsignacion}</td>
                          <td>{item.asignadoA}</td>
                          <td>
                            {item.fechaAsignacion
                              ? new Date(
                                  item.fechaAsignacion
                                ).toLocaleString()
                              : "N/A"}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="modal-actions">
                  <button
                    type="button"
                    className="btn-cancelar"
                    onClick={handleCloseHistoricalModal}
                  >
                    Cerrar
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </main>
  </div>
);

export default InformeModulo;
