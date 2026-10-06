import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { initials, makeId, Modal, PageHeading, SearchInput, useHospital, type Patient } from '../hospital'

export const Route = createFileRoute('/pacientes')({ component: Pacientes })

function Pacientes() {
  const { data, updateData } = useHospital()
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Patient | null>(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const patients = data.patients.filter((patient) =>
    `${patient.name} ${patient.document} ${patient.phone}`.toLowerCase().includes(query.toLowerCase()),
  )

  function openForm(patient?: Patient) {
    setEditing(patient ?? null)
    setError('')
    setOpen(true)
  }

  function savePatient(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const patient: Patient = {
      id: editing?.id ?? makeId('p'),
      name: String(form.get('name')).trim(),
      document: String(form.get('document')).trim(),
      birthDate: String(form.get('birthDate')),
      phone: String(form.get('phone')).trim(),
      bloodType: String(form.get('bloodType')),
    }
    const duplicate = data.patients.some((item) => item.id !== patient.id && item.document === patient.document)
    if (duplicate) return setError('Já existe um paciente cadastrado com este documento.')
    updateData((current) => ({
      ...current,
      patients: editing
        ? current.patients.map((item) => item.id === patient.id ? patient : item)
        : [patient, ...current.patients],
    }))
    setNotice(editing ? 'Cadastro do paciente atualizado.' : 'Paciente cadastrado com sucesso.')
    setOpen(false)
  }

  function removePatient(patient: Patient) {
    const hasHistory = data.appointments.some((item) => item.patientId === patient.id)
      || data.hospitalizations.some((item) => item.patientId === patient.id)
    if (hasHistory) return setNotice('Este paciente possui atendimentos no histórico e não pode ser removido.')
    if (!window.confirm(`Remover o cadastro de ${patient.name}?`)) return
    updateData((current) => ({ ...current, patients: current.patients.filter((item) => item.id !== patient.id) }))
    setNotice('Cadastro removido.')
  }

  return (
    <>
      <PageHeading
        eyebrow="CADASTRO E PRONTUÁRIOS"
        title="Pacientes"
        description="Gerencie os cadastros e consulte os dados essenciais dos pacientes."
        action={<button className="button" onClick={() => openForm()}><span className="button-plus">+</span> Novo paciente</button>}
      />
      <section className="metric-grid compact-metrics">
        <article className="metric-card"><div className="metric-icon">♙</div><div className="metric-copy"><span>Total de pacientes</span><strong>{data.patients.length}</strong><small>Cadastros no sistema</small></div></article>
        <article className="metric-card"><div className="metric-icon blue">▦</div><div className="metric-copy"><span>Consultas agendadas</span><strong>{data.appointments.filter((item) => item.status === 'Agendada').length}</strong><small>Em acompanhamento</small></div></article>
        <article className="metric-card"><div className="metric-icon gold">▣</div><div className="metric-copy"><span>Internações ativas</span><strong>{data.hospitalizations.filter((item) => item.status === 'Internado').length}</strong><small>Vinculadas a pacientes</small></div></article>
        <article className="metric-card"><div className="metric-icon violet">⌕</div><div className="metric-copy"><span>Resultados</span><strong>{patients.length}</strong><small>{query ? 'Correspondem à busca' : 'Exibidos nesta lista'}</small></div></article>
      </section>
      {notice && <div className="inline-notice">{notice}<button className="notice-close" onClick={() => setNotice('')} aria-label="Fechar aviso">×</button></div>}
      <section className="panel records-panel">
        <div className="panel-header records-header">
          <div><h2>Todos os pacientes</h2><p>Pesquise, atualize ou revise um cadastro.</p></div>
          <SearchInput value={query} onChange={setQuery} placeholder="Buscar por nome, documento ou telefone" />
        </div>
        <div className="table-wrap">
          <table className="data-table">
            <thead><tr><th>Paciente</th><th>Documento</th><th>Data de nascimento</th><th>Tipo sanguíneo</th><th>Contato</th><th aria-label="Ações" /></tr></thead>
            <tbody>
              {patients.map((patient) => (
                <tr key={patient.id}>
                  <td><div className="person-cell"><span className="person-avatar">{initials(patient.name)}</span><span><strong>{patient.name}</strong><small>Paciente</small></span></div></td>
                  <td>{patient.document}</td><td>{new Date(`${patient.birthDate}T12:00:00`).toLocaleDateString('pt-BR')}</td><td><span className="blood-type">{patient.bloodType}</span></td><td>{patient.phone}</td>
                  <td><div className="table-actions"><button className="button ghost small" onClick={() => openForm(patient)}>Editar</button><button className="button ghost small delete-link" onClick={() => removePatient(patient)}>Remover</button></div></td>
                </tr>
              ))}
              {patients.length === 0 && <tr><td colSpan={6}><div className="empty-state"><strong>Nenhum paciente encontrado</strong>Tente outro nome ou documento.</div></td></tr>}
            </tbody>
          </table>
        </div>
        <div className="table-footer"><span>Mostrando {patients.length} de {data.patients.length} pacientes</span><span>Atualizado agora</span></div>
      </section>

      {open && <Modal title={editing ? 'Editar paciente' : 'Novo paciente'} description="Os dados serão salvos neste navegador." onClose={() => setOpen(false)}>
        <form onSubmit={savePatient}>
          <div className="form-grid">
            <label className="form-field full"><span>Nome completo *</span><input name="name" defaultValue={editing?.name} placeholder="Ex.: Marina Oliveira" required autoFocus /></label>
            <label className="form-field"><span>CPF *</span><input name="document" defaultValue={editing?.document} placeholder="000.000.000-00" required /></label>
            <label className="form-field"><span>Data de nascimento *</span><input type="date" name="birthDate" defaultValue={editing?.birthDate} max={new Date().toLocaleDateString('en-CA')} required /></label>
            <label className="form-field"><span>Telefone *</span><input type="tel" name="phone" defaultValue={editing?.phone} placeholder="(11) 99999-9999" required /></label>
            <label className="form-field"><span>Tipo sanguíneo</span><select name="bloodType" defaultValue={editing?.bloodType ?? ''}><option value="">Não informado</option>{['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map((type) => <option key={type}>{type}</option>)}</select></label>
          </div>
          {error && <div className="inline-notice error">{error}</div>}
          <div className="form-actions"><button className="button secondary" type="button" onClick={() => setOpen(false)}>Cancelar</button><button className="button" type="submit">{editing ? 'Salvar alterações' : 'Cadastrar paciente'}</button></div>
        </form>
      </Modal>}
    </>
  )
}
