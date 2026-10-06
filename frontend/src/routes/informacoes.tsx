import { createFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { formatDate, patientName, professionalName, roomName, roomOccupancy, StatusBadge, useHospital } from '../hospital'

export const Route = createFileRoute('/informacoes')({ component: Informacoes })

function Informacoes() {
  const { data } = useHospital()
  const [selectedId, setSelectedId] = useState('p1')
  const patient = data.patients.find((item) => item.id === selectedId) ?? data.patients[0]
  const patientAppointments = patient ? data.appointments
    .filter((item) => item.patientId === patient.id)
    .sort((a, b) => `${b.date}${b.time}`.localeCompare(`${a.date}${a.time}`)) : []
  const patientStays = patient ? data.hospitalizations
    .filter((item) => item.patientId === patient.id)
    .sort((a, b) => b.admittedAt.localeCompare(a.admittedAt)) : []
  const activeStays = data.hospitalizations.filter((stay) => stay.status === 'Internado').length
  const beds = data.rooms.reduce((sum, room) => sum + room.capacity, 0)
  const occupied = data.rooms.reduce((sum, room) => sum + roomOccupancy(data, room.id), 0)
  const today = new Date().toLocaleDateString('en-CA')
  const todayAppointments = data.appointments.filter((item) => item.date === today && item.status === 'Agendada').length

  return (
    <>
      <div className="page-heading">
        <div><p className="eyebrow">CONSULTA DE DADOS</p><h1>Informações</h1><p className="page-description">Acesse o histórico clínico do paciente e o resumo administrativo da unidade.</p></div>
        <span className="demo-tag"><span /> Dados de demonstração</span>
      </div>
      <div className="info-grid">
        <section className="panel">
          <div className="panel-header"><div><h2>Prontuário do paciente</h2><p>Consultas e internações vinculadas ao cadastro</p></div><span className="record-label">HISTÓRICO</span></div>
          <div className="panel-body">
            <label className="form-field patient-picker"><span>Selecionar paciente</span><select value={patient?.id ?? ''} onChange={(event) => setSelectedId(event.target.value)} disabled={!data.patients.length}><option value="" disabled>Selecione um paciente</option>{data.patients.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
            {patient ? <>
              <div className="patient-record-head"><span className="record-avatar">{patient.name.split(' ').slice(0, 2).map((part) => part[0]).join('').toUpperCase()}</span><div><strong>{patient.name}</strong><small>Prontuário · {patient.document}</small></div><span className="blood-type">{patient.bloodType}</span></div>
              <div className="patient-summary">
                <div><span>Nascimento</span><strong>{formatDate(patient.birthDate)}</strong></div>
                <div><span>Telefone</span><strong>{patient.phone}</strong></div>
                <div><span>Tipo sanguíneo</span><strong>{patient.bloodType || 'Não informado'}</strong></div>
              </div>
              <div className="history-heading"><h3>Linha do tempo</h3><span>{patientAppointments.length + patientStays.length} eventos</span></div>
              <div className="timeline">
                {[
                  ...patientAppointments.map((item) => ({ date: item.date, time: item.time, key: item.id, kind: 'Consulta', title: `${item.type} · ${professionalName(data, item.professionalId)}`, detail: item.notes, status: item.status })),
                  ...patientStays.map((item) => ({ date: item.admittedAt, time: '', key: item.id, kind: 'Internação', title: `${roomName(data, item.roomId)} · ${item.reason}`, detail: item.dischargedAt ? `Entrada em ${formatDate(item.admittedAt)} · alta em ${formatDate(item.dischargedAt)}` : `Entrada em ${formatDate(item.admittedAt)} · em andamento`, status: item.status })),
                ].sort((a, b) => `${b.date}${b.time}`.localeCompare(`${a.date}${a.time}`)).map((event) => <div className="timeline-item" key={event.key}><div className="timeline-date">{formatDate(event.date)}{event.time && <small>{event.time}</small>}</div><div><div className="timeline-title-row"><strong>{event.title}</strong><StatusBadge>{event.status}</StatusBadge></div><p>{event.detail || event.kind}</p></div></div>)}
                {patientAppointments.length + patientStays.length === 0 && <div className="empty-state"><strong>Sem histórico registrado</strong>Consultas e internações futuras ficarão disponíveis nesta linha do tempo.</div>}
              </div>
            </> : <div className="empty-state"><strong>Nenhum paciente cadastrado</strong>Cadastre um paciente para consultar o histórico.</div>}
          </div>
        </section>
        <div className="admin-column">
          <section className="panel">
            <div className="panel-header"><div><h2>Resumo da unidade</h2><p>Indicadores administrativos atuais</p></div></div>
            <div className="panel-body">
              <div className="detail-list">
                <div className="detail-row"><span>Profissionais cadastrados</span><strong>{data.professionals.length}</strong></div>
                <div className="detail-row"><span>Especialidades disponíveis</span><strong>{new Set(data.professionals.map((item) => item.specialty)).size}</strong></div>
                <div className="detail-row"><span>Consultas para hoje</span><strong>{todayAppointments}</strong></div>
                <div className="detail-row"><span>Internações ativas</span><strong>{activeStays}</strong></div>
                <div className="detail-row"><span>Quartos cadastrados</span><strong>{data.rooms.length}</strong></div>
              </div>
              <div className="admin-occupancy"><div><span>Ocupação de leitos</span><strong>{occupied} <small>/ {beds}</small></strong></div><div className="progress-track"><div className="progress-fill" style={{ width: `${beds ? occupied / beds * 100 : 0}%` }} /></div><small>{Math.max(0, beds - occupied)} leitos disponíveis</small></div>
            </div>
          </section>
          <section className="panel resources-panel">
            <div className="panel-header"><div><h2>Acesso rápido</h2><p>Atualize os registros da unidade</p></div></div>
            <div className="resource-links">
              <Link to="/pacientes"><span className="resource-icon">♙</span><span><strong>Pacientes</strong><small>{data.patients.length} cadastros</small></span><b>→</b></Link>
              <Link to="/profissionais"><span className="resource-icon blue">⚕</span><span><strong>Profissionais</strong><small>{data.professionals.length} cadastros</small></span><b>→</b></Link>
              <Link to="/quartos"><span className="resource-icon gold">⌂</span><span><strong>Quartos e leitos</strong><small>{Math.max(0, beds - occupied)} vagas</small></span><b>→</b></Link>
            </div>
          </section>
          <div className="inline-notice info-notice">Os dados são salvos localmente no navegador e servem para acompanhar o protótipo antes da integração com o backend.</div>
        </div>
      </div>
    </>
  )
}
