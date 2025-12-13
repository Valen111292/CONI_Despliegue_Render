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
    const [filterStatus, setFilterStatus] = useState('all');

    // --- ESTADOS PARA INFORMES HISTÓRICOS ---
    const [historicalReports, setHistoricalReports] = useState([]);
    const [cargandoHistorico, setCargandoHistorico] = useState(false);
    const [errorHistorico, setErrorHistorico] = useState('');
    const [selectedHistoricalReportData, setSelectedHistoricalReportData] = useState(null);
    const [isHistoricalModalOpen, setIsHistoricalModalOpen] = useState(false);

    const assignmentStatusOptions = [
        { value: 'all', label: 'Todos los Estados' },
        { value: 'ASIGNADO', label: 'Asignado' },
        { value: 'DISPONIBLE', label: 'Disponible' },
        { value: 'PENDIENTE', label: 'Pendiente' }
    ];

    // ===============================
    // HEADERS AUTENTICACIÓN (iOS FIX)
    // ===============================
    const getAuthHeaders = () => {
        const storedUserJSON = localStorage.getItem("usuarioLogueado");
        const usuario = storedUserJSON ? JSON.parse(storedUserJSON) : null;

        return {
            'Content-Type': 'application/json',
            'X-User-Id': usuario?.idUsuario || usuario?.id || ''
        };
    };

    // ===============================
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

            if (!id || !rol || !cargo || rol !== 'admin') {
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
                throw new Error('Error al cargar informe');
            }

            const data = await response.json();
            setReportData(data);
        } catch (err) {
            setErrorReporte(err.message);
        } finally {
            setCargandoReporte(false);
        }
    }, [currentUser, filterStatus]);

    // ===============================
    // HISTÓRICOS
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
                throw new Error('Error al cargar históricos');
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
        if (reportData.length === 0) return;

        await fetch('https://coni-backend.onrender.com/informes/guardar', {
            method: 'POST',
            credentials: 'include',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                estadoFiltro: filterStatus,
                reporteJson: JSON.stringify(reportData)
            })
        });

        fetchHistoricalReports();
        exportToExcel(reportData, `Informe_Inventario_${filterStatus}`);
    };

    // ===============================
    // HISTÓRICO DETALLE
    // ===============================
    const fetchHistoricalDetail = async (id, download = false) => {
        const response = await fetch(
            `https://coni-backend.onrender.com/informes/historico?id=${id}`,
            {
                credentials: 'include',
                headers: getAuthHeaders()
            }
        );

        const data = await response.json();

        if (download) {
            exportToExcel(data, `Informe_Historico_${id}`);
        } else {
            setSelectedHistoricalReportData(data);
            setIsHistoricalModalOpen(true);
        }
    };

    const handleViewHistorical = (id) => {
        fetchHistoricalDetail(id, false);
    };

    const handleDownloadHistorical = (id) => {
        fetchHistoricalDetail(id, true);
    };

    const handleCloseHistoricalModal = () => {
        setIsHistoricalModalOpen(false);
        setSelectedHistoricalReportData(null);
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
                <button onClick={handleLogout}>Cerrar sesión</button>
            </header>

            <main>
                <h2>Generar Informes</h2>

                {cargandoUsuario ? <p>Cargando usuario...</p> : (
                    <>
                        <button onClick={fetchCurrentReport}>Actualizar</button>

                        {reportData.length > 0 && (
                            <button onClick={handleGenerateAndDownload}>
                                Generar y Descargar Excel
                            </button>
                        )}

                        <h3>Históricos</h3>
                        <ul>
                            {historicalReports.map(r => (
                                <li key={r.id}>
                                    {r.id}
                                    <button onClick={() => handleViewHistorical(r.id)}>Ver</button>
                                    <button onClick={() => handleDownloadHistorical(r.id)}>Excel</button>
                                </li>
                            ))}
                        </ul>

                        {isHistoricalModalOpen && (
                            <button onClick={handleCloseHistoricalModal}>Cerrar</button>
                        )}
                    </>
                )}
            </main>
        </div>
    );
};

export default InformeModulo;
