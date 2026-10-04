import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/profissionais')({
  component: Profissionais,
})

function Profissionais() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Profissionais</h1>
      <p className="mt-4">Tela de profissionais da saúde.</p>
    </main>
  )
}
