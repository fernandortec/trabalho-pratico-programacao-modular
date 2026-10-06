import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { initials, makeId, Modal, PageHeading, SearchInput, useHospital, type Professional } from '../hospital'

export const Route = createFileRoute('/profissionais')({ component: Profissionais })

const specialties = ['Cardiologia', 'Clínica geral', 'Dermatologia', 'Ginecologia', 'Neurologia', 'Ortopedia', 'Pediatria', 'Psiquiatria']

function Profissionais() {
  const { data, updateData } = useHospital()
  const [query, setQuery] = useState('')
  const [specialty, setSpecialty] = useState('Todas as especialidades')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Professional | null>(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const professionals = data.professionals.filter((person) =>
    `${person.name} ${person.registration} ${person.specialty}`.toLowerCase().includes(query.toLowerCase())
    && (specialty === 'Todas as especialidades' || person.specialty === specialty),
  )
  const upcoming = (id: string) => data.appointments.filter((appointment) => appointment.professionalId === id && appointment.status === 'Agendada').length

  function openForm(person?: Professional) {
    setEditing(person ?? null)
    setError('')
    setOpen(true)
  }

  function saveProfessional(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const professional: Professional = {
      id: editing?.id ?? makeId('d'),
      name: String(form.get('name')).trim(),
      registration: String(form.get('registration')).trim(),
      specialty: String(form.get('specialty')),
      unit: String(form.get('unit')).trim(),
    }
    if (data.professionals.some((item) => item.id !== professional.id && item.registration.toLowerCase() === professional.registration.toLowerCase())) {
      return setError('Já existe um profissional cadastrado com este registro.')
    }
    updateData((current) => ({
      ...current,
      professionals: editing
        ? current.professionals.map((item) => item.id === professional.id ? professional : item)
        : [professional, ...current.professionals],
    }))
    setNotice(editing ? 'Cadastro atualizado.' : 'Profissional cadastrado com sucesso.')
    setOpen(false)
  }

  function removeProfessional(person: Professional) {
    if (data.appointments.some((item) => item.professionalId === person.id)) return setNotice('Este profissional tem consultas no histórico e não pode ser removido.')
    if (!window.confirm(`Remover ${person.name} do cadastro?`)) return
    updateData((current) => ({ ...current, professionals: current.professionals.filter((item) => item.id !== person.id) }))
    setNotice('Profissional removido.')
  }

  return (
    <>
      <PageHeading eyebrow="EQUIPE ASSISTENCIAL" title="Profissionais" description="Mantenha a equipe e suas especialidades organizadas para a agenda." action={<button className="button" onClick={() => openForm()}><span className="button-plus">+</span> Novo profissional</button>} />
      <section className="metric-grid compact-metrics">
        <article className="metric-card"><div className="metric-icon blue">⚕</div><div className="metric-copy"><span>Profissionais ativos</span><strong>{data.professionals.length}</strong><small>Equipe cadastrada</small></div></article>
        <article className="metric-card"><div className="metric-icon">✳</div><div className="metric-copy"><span>Especialidades</span><strong>{new Set(data.professionals.map((person) => person.specialty)).size}</strong><small>Áreas de atendimento</small></div></article>
        <article className="metric-card"><div className="metric-icon gold">▦</div><div className="metric-copy"><span>Consultas futuras</span><strong>{data.appointments.filter((item) => item.status === 'Agendada').length}</strong><small>Na agenda da unidade</small></div></article>
        <article className="metric-card"><div className="metric-icon violet">⌕</div><div className="metric-copy"><span>Resultados</span><strong>{professionals.length}</strong><small>Profissionais exibidos</small></div></article>
      </section>
      {notice && <div className="inline-notice">{notice}<button className="notice-close" onClick={() => setNotice('')} aria-label="Fechar aviso">×</button></div>}
      <section className="panel records-panel">
        <div className="panel-header records-header"><div><h2>Equipe médica</h2><p>Pesquise por nome, CRM ou especialidade.</p></div><div className="toolbar-inline"><SearchInput value={query} onChange={setQuery} placeholder="Buscar profissional" /><select className="filter-select" value={specialty} onChange={(event) => setSpecialty(event.target.value)}><option>Todas as especialidades</option>{specialties.map((item) => <option key={item}>{item}</option>)}</select></div></div>
        <div className="table-wrap"><table className="data-table"><thead><tr><th>Profissional</th><th>Registro</th><th>Especialidade</th><th>Unidade</th><th>Agenda futura</th><th aria-label="Ações" /></tr></thead><tbody>
          {professionals.map((person, index) => { const appointments = upcoming(person.id); return <tr key={person.id}><td><div className="person-cell"><span className={`person-avatar ${index % 2 ? 'blue' : ''}`}>{initials(person.name.replace(/^(Dra?\.\s*)/i, ''))}</span><span><strong>{person.name}</strong><small>Corpo clínico</small></span></div></td><td>{person.registration}</td><td><span className="specialty-chip">{person.specialty}</span></td><td>{person.unit}</td><td>{appointments} {appointments === 1 ? 'consulta' : 'consultas'}</td><td><div className="table-actions"><button className="button ghost small" onClick={() => openForm(person)}>Editar</button><button className="button ghost small delete-link" onClick={() => removeProfessional(person)}>Remover</button></div></td></tr> })}
          {professionals.length === 0 && <tr><td colSpan={6}><div className="empty-state"><strong>Nenhum profissional encontrado</strong>Ajuste a busca ou o filtro de especialidade.</div></td></tr>}
        </tbody></table></div>
        <div className="table-footer"><span>Mostrando {professionals.length} de {data.professionals.length} profissionais</span><span>Cadastro da unidade central</span></div>
      </section>
      {open && <Modal title={editing ? 'Editar profissional' : 'Novo profissional'} description="Cadastre os dados que serão usados na agenda de consultas." onClose={() => setOpen(false)}><form onSubmit={saveProfessional}><div className="form-grid">
        <label className="form-field full"><span>Nome completo *</span><input name="name" defaultValue={editing?.name} placeholder="Ex.: Dra. Ana Ribeiro" required autoFocus /></label>
        <label className="form-field"><span>Registro profissional *</span><input name="registration" defaultValue={editing?.registration} placeholder="CRM 000.000" required /></label>
        <label className="form-field"><span>Especialidade *</span><select name="specialty" defaultValue={editing?.specialty ?? ''} required><option value="" disabled>Selecione uma especialidade</option>{specialties.map((item) => <option key={item}>{item}</option>)}</select></label>
        <label className="form-field full"><span>Unidade / setor *</span><input name="unit" defaultValue={editing?.unit} placeholder="Ex.: Ambulatório A" required /></label>
      </div>{error && <div className="inline-notice error">{error}</div>}<div className="form-actions"><button className="button secondary" type="button" onClick={() => setOpen(false)}>Cancelar</button><button className="button" type="submit">{editing ? 'Salvar alterações' : 'Cadastrar profissional'}</button></div></form></Modal>}
    </>
  )
}
