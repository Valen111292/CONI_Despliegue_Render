import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from "react-router-dom";
import './estilos.css';
import logo from '../img/ESLOGAN CONI.png';

function Equipo() {
  const navigate = useNavigate();
  const [tipo, setTipo] = useState('');
  const [clase, setClase] = useState('');
  const [equipos, setEquipos] = useState([]);
  const [filtro, setFiltro] = useState('');
  const [editando, setEditando] = useState(false);
  const [equipoEditando, setEquipoEditando] = useState(null);
  const [n_inventario, setN_inventario] = useState('');
  const [n_serie, setN_Serie] = useState('');
  const [marca, setMarca] = useState('');
  const [ram, setRam] = useState('');
  const [disco, setDisco] = useState('');
  const [procesador, setProcesador] = useState('');
  const [estado, setEstado] = useState('');
  const [modoEdicion, setModoEdicion] = useState(false);

  const tipoPorClases = {
    periferico: ['MOUSE', 'TECLADO', 'MONITOR', 'IMPRESORA', 'PROYECTOR', 'PARLANTE'],
    equipo: ['LAPTOP', 'ESCRITORIO', 'TABLET'],
  };

  const fetchEquipos = useCallback(async () => {
    try {
      let url = "http://localhost:8080/CONI1.0/EquipoServlet?accion=listar";
      if (filtro) url += `&estado=${encodeURIComponent(filtro.toUpperCase())}`;

      const response = await fetch(url);
      if (!response.ok) throw new Error("Error al cargar equipos");

      const data = await response.json();
      setEquipos(data);
    } catch (error) {
      console.error("Error al listar equipos:", error);
    }
  }, [filtro]);

  useEffect(() => {
    fetchEquipos();
  }, [fetchEquipos]);

  useEffect(() => {
    const usuario = localStorage.getItem("usuarioLogueado");
    if (!usuario) {
      navigate("/login");
    }
  }, [navigate]);

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/CONI1.0/LogoutServlet", {
        method: "GET",
        credentials: "include"
      });

      if (response.ok) {
        localStorage.removeItem("usuarioLogueado");
        localStorage.removeItem("rol");
        sessionStorage.clear();
        localStorage.setItem("logoutMessage", "Sesión cerrada exitosamente");
        navigate("/");
      } else {
        console.error("Error al cerrar sesión, status:", response.status);
      }
    } catch (error) {
      console.error("Error al cerrar sesión", error);
    }
  };

  const verificarSerieUnica = async (serie) => {
    const res = await fetch(`http://localhost:8080/CONI1.0/EquipoServlet?verificarSerie=${serie}`);
    const data = await res.json();
    return !data.existe;
  };

  const limpiarCampos = () => {
    setClase('');
    setTipo('');
    setN_Serie('');
    setMarca('');
    setRam('');
    setDisco('');
    setProcesador('');
    setEstado('');
  };

  const handleAgregar = async () => {
    if (!tipo || !clase) {
      alert('Por favor, seleccione un tipo y una clase.');
      return;
    }

    if (!(await verificarSerieUnica(n_serie))) {
      alert('El número de serie ya existe.');
      return;
    }

    const body = {
      n_serie,
      tipo,
      clase,
      marca,
      ram,
      disco,
      procesador,
      estado,
    };

    try {
      const res = await fetch('http://localhost:8080/CONI1.0/EquipoServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      const data = await res.json();

      if (res.ok) {
        alert('Equipo registrado correctamente');
        limpiarCampos();
        fetchEquipos(); // Recargar lista
      } else {
        alert('Error al registrar: ' + data.message);
      }
    } catch (error) {
      alert('Error al conectar con el servidor.');
    }
  };

  const limpiarFormulario = () => {
    setN_Serie('');
    setClase('');
    setTipo('');
    setMarca('');
    setRam('');
    setDisco('');
    setProcesador('');
    setEstado('');
    setN_inventario('');
  };


  const handleActualizar = async () => {
    const equipoActualizado = { ...equipoEditando };

    try {
      const res = await fetch('http://localhost:8080/CONI1.0/EquipoServlet', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(equipoActualizado),
      });

      if (res.ok) {
        alert('Equipo actualizado exitosamente.');
        setEditando(false);
        setEquipoEditando(null);
        limpiarFormulario();
        fetchEquipos();
      } else {
        const data = await res.json();
        alert('Error al actualizar: ' + data.message);
      }
    } catch (error) {
      console.error('Error al actualizar:', error);
      alert('Error al actualizar equipo.');
    }
  };



  const handleEliminar = async (n_inventario) => {
    const confirmar = window.confirm('¿Seguro que deseas eliminar este equipo?');
    if (!confirmar) return;

    const res = await fetch(`http://localhost:8080/CONI1.0/EquipoServlet?n_inventario=${n_inventario}`, {
      method: 'DELETE',
    });

    if (res.ok) {
      alert('Equipo eliminado.');
      fetchEquipos();
    }
  };

  const iniciarEdicion = (equipo) => {
    setEditando(true);
    setEquipoEditando(equipo);
    setN_inventario(equipo.n_inventario);
    setN_Serie(equipo.n_serie);
    setClase(equipo.clase);
    setTipo(tipoPorClases.periferico.includes(equipo.tipo.toUpperCase()) ? 'periferico' : 'equipo');
    setMarca(equipo.marca);
    setRam(equipo.ram);
    setDisco(equipo.disco);
    setProcesador(equipo.procesador);
    setEstado(equipo.estado);
    setModoEdicion(true);
  };

  const cancelarEdicion = () => {
    setEditando(false);
    setEquipoEditando(null);
    setClase('');
    setTipo('');
  };

  return (
    <div>
      <div className="encabezado">
        <img src={logo} className="imagen-encabezado" alt="Logo CONI" />
        <div className="barra-superior">
          <nav>
            <li><button onClick={handleLogout}>Cerrar sesión</button></li>
          </nav>
        </div>
      </div>

      <h1 className='tituloGestionEquipo'>Gestión de Equipos CONI</h1>
      <h2 className='subtituloGestionEquipo'>{editando ? 'Editar Equipo' : 'Agregar Nuevo Equipo'}</h2>

      <form className='agregarEquipo' onSubmit={(e) => {
        e.preventDefault();
        editando ? handleActualizar() : handleAgregar();
      }}>
        {modoEdicion && (
          <div className="form-group">
            <input type="text" value={n_inventario} disabled />
          </div>
        )}
        <input name="n_serie" placeholder="Número Serie"
          value={editando ? equipoEditando.n_serie : n_serie} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, n_serie: e.target.value }) : setN_Serie(e.target.value)}
          required />
          
          <select className='clase'
            value={editando ? equipoEditando.clase : clase}
            onChange={(e) => {
              if (editando) {
                setEquipoEditando({ ...equipoEditando, clase: e.target.value });
              } else {
                setClase(e.target.value);
              }
            }}
          >
            <option value="">-- Selecciona una clase --</option>
            <option value="periferico">PERIFÉRICO</option>
            <option value="equipo">EQUIPO</option>
          </select>

          <select className='tipo'
            value={editando ? equipoEditando.tipo : tipo}
            onChange={(e) => {
              if (editando) {
                setEquipoEditando({ ...equipoEditando, tipo: e.target.value });
              } else {
                setTipo(e.target.value);
              }
            }}
          >
            <option value="">-- Selecciona un tipo --</option>
            {(editando ? tipoPorClases[equipoEditando.clase] : tipoPorClases[clase])?.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>

          <input name="marca" placeholder="Marca" value={editando ? equipoEditando.marca : marca} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, marca: e.target.value }) : setMarca(e.target.value)} required />

          <input name="ram" placeholder="RAM" value={editando ? equipoEditando.ram : ram} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, ram: e.target.value }) : setRam(e.target.value)} />

          <input name="disco" placeholder="Disco" value={editando ? equipoEditando.disco : disco} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, disco: e.target.value }) : setDisco(e.target.value)} />

          <input name="procesador" placeholder="Procesador" value={editando ? equipoEditando.procesador : procesador} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, procesador: e.target.value }) : setProcesador(e.target.value)} />

          <select className='estado' name="estado" value={editando ? equipoEditando.estado : estado} onChange={(e) => editando ? setEquipoEditando({ ...equipoEditando, estado: e.target.value }) : setEstado(e.target.value)} required>
            <option value="">-- Selecciona un estado --</option>
            <option value="DISPONIBLE">DISPONIBLE</option>
            <option value="ASIGNADO">ASIGNADO</option>
            <option value="PENDIENTE">PENDIENTE</option>
          </select>

          <button className='botonEquipo' type="submit">{editando ? 'Actualizar' : 'Agregar'}</button>
          {editando && <button type="button" onClick={cancelarEdicion}>Cancelar</button>}
        </form>

        <div className='filtrar'>
          <label className='filtrarEquipo'>Filtrar por estado:</label>
          <select onChange={(e) => setFiltro(e.target.value)} value={filtro}>
            <option value="">TODOS</option>
            <option value="disponible">DISPONIBLE</option>
            <option value="asignado">ASIGNADO</option>
            <option value="pendiente">PENDIENTE</option>
          </select>
        </div>

        <table border="1" style={{ marginTop: '20px' }}>
          <thead>
            <tr>
              <th>Inventario</th>
              <th>Número de Serie</th>
              <th>Clase</th>
              <th>Tipo</th>
              <th>Marca</th>
              <th>RAM</th>
              <th>Disco</th>
              <th>Procesador</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {Array.isArray(equipos) && equipos.map((equipo) => (
              <tr key={equipo.n_inventario}>
                <td>{equipo.n_inventario}</td>
                <td>{equipo.n_serie}</td>
                <td>{equipo.clase}</td>
                <td>{equipo.tipo}</td>
                <td>{equipo.marca}</td>
                <td>{equipo.ram}</td>
                <td>{equipo.disco}</td>
                <td>{equipo.procesador}</td>
                <td>{equipo.estado}</td>
                <td>
                  <div className='acciones'>
                    <button className='editar' onClick={() => iniciarEdicion(equipo)}>Editar</button>
                    <button className='eliminar' onClick={() => handleEliminar(equipo.n_inventario)}>Eliminar</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
    </div>
  );
}

export default Equipo;
