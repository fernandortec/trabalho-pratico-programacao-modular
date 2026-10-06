import { Link, createFileRoute } from '@tanstack/react-router'
import { dayLabel, formatDate, MetricCard, PageHeading, patientName, professionalName, roomOccupancy, StatusBadge, useHospital } from '../hospital'

export const Route = createFileRoute('/')({ component: Dashboard })

function Dashboard() {
  const { data } = useHospital()
  const todayDate = new Date()
  const today = new Date().toLocaleDateString('en-CA')
  const weekday = todayDate.toLocaleDateString('pt-BR', { weekday: 'long' }).toLocaleUpperCase('pt-BR')
  const appointments = data.appointments
    .filter((appointment) => appointment.status === 'Agendada' && appointment.date >= today)
    .sort((a, b) => `${a.date}${a.time}`.localeCompare(`${b.date}${b.time}`))
  const admissions = data.hospitalizations.filter((stay) => stay.status === 'Internado').length
  const occupied = data.rooms.reduce((sum, room) => sum + roomOccupancy(data, room.id), 0)
  const beds = data.rooms.reduce((sum, room) => sum + room.capacity, 0)
  const occupancyRate = beds ? Math.round((occupied / beds) * 100) : 0
  const sectors = [...new Set(data.rooms.map((room) => room.sector))]

  return (
    <>
      <PageHeading
        eyebrow={`${weekday} · VISÃO GERAL`}
        title="Bom dia, equipe 👋"
        description="Acompanhe a operação do hospital e acesse as principais atividades."
        action={<span className="date-chip">{formatDate(today)}</span>}
      />

      <section className="metric-grid" aria-label="Resumo operacional">
        <MetricCard label="Pacientes cadastrados" value={data.patients.length} note="Registros ativos" icon="♙" />
        <MetricCard label="Consultas próximas" value={appointments.length} note="Agendamentos em aberto" icon="▦" accent="blue" />
        <MetricCard label="Em internação" value={admissions} note="Pacientes internados" icon="▣" accent="gold" />
        <MetricCard label="Leitos ocupados" value={`${occupied}/${beds}`} note={`${occupancyRate}% da capacidade`} icon="⌂" accent="violet" />
      </section>

      <section className="content-grid">
        <article className="panel">
          <div className="panel-header">
            <div><h2>Próximas consultas</h2><p>Agenda confirmada da unidade</p></div>
            <Link to="/consultas" className="text-link">Ver agenda →</Link>
          </div>
          <div className="panel-body appointment-list">
            {appointments.slice(0, 5).map((appointment) => (
              <div className="appointment-row" key={appointment.id}>
                <div className="time-block">{appointment.time}</div>
                <div>
                  <strong>{patientName(data, appointment.patientId)}</strong>
                  <small>{professionalName(data, appointment.professionalId)} · {dayLabel(appointment.date)}</small>
                </div>
                <StatusBadge>{appointment.status}</StatusBadge>
              </div>
            ))}
            {appointments.length === 0 && <div className="empty-state">Não há consultas futuras na agenda.</div>}
          </div>
        </article>

        <article className="panel">
          <div className="panel-header"><div><h2>Ocupação hospitalar</h2><p>Disponibilidade de leitos por setor</p></div></div>
          <div className="occupancy-card">
            <div className="occupancy-top">
              <div><h3>Leitos ocupados</h3><p>Visão atual da unidade</p></div>
              <div className="occupancy-number">{occupancyRate}<small>%</small></div>
            </div>
            <div className="progress-track"><div className="progress-fill" style={{ width: `${occupancyRate}%` }} /></div>
            <div className="sector-list">
              {sectors.map((sector) => {
                const rooms = data.rooms.filter((room) => room.sector === sector)
                const occupiedInSector = rooms.reduce((sum, room) => sum + roomOccupancy(data, room.id), 0)
                const capacity = rooms.reduce((sum, room) => sum + room.capacity, 0)
                return <div className="sector-row" key={sector}><span>{sector}</span><strong>{occupiedInSector} de {capacity} leitos</strong></div>
              })}
            </div>
          </div>
        </article>
      </section>

      <section className="quick-links" aria-label="Acesso rápido">
        <Link to="/pacientes" className="quick-link"><span className="quick-link-icon">♙</span><span><strong>Cadastrar paciente</strong><small>Adicionar ao prontuário</small></span></Link>
        <Link to="/consultas" className="quick-link"><span className="quick-link-icon">▦</span><span><strong>Agendar consulta</strong><small>Ver disponibilidade médica</small></span></Link>
        <Link to="/internacoes" className="quick-link"><span className="quick-link-icon">▣</span><span><strong>Nova internação</strong><small>Consultar leitos disponíveis</small></span></Link>
      </section>
    </>
  )
}
