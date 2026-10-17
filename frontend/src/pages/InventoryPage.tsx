import { useState, useEffect, useCallback, type FormEvent } from 'react';
import { Search, Plus, Pencil, Trash2, Loader2, Package, X } from 'lucide-react';
import toast from 'react-hot-toast';
import Modal from '../components/Modal';
import { getAllDrugs, searchDrugs, addDrug, updateDrug, deleteDrug } from '../api/drugs';
import type { Drug, AddDrugRequest, UpdateDrugRequest } from '../types';

// ── helpers ──────────────────────────────────────────────────────────────────

const emptyAdd = (): AddDrugRequest => ({
  name: '',
  genericName: '',
  strength: '',
  dosageForm: '',
  brand: '',
  unitPrice: 0,
  costPrice: 0,
  purchaseQuantity: 0,
  expiryDate: '',
});

type ModalState =
  | { type: 'none' }
  | { type: 'add' }
  | { type: 'edit'; drug: Drug }
  | { type: 'delete'; drug: Drug };

// ── component ─────────────────────────────────────────────────────────────────

export default function InventoryPage() {
  const [drugs, setDrugs] = useState<Drug[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchWord, setSearchWord] = useState('');
  const [searching, setSearching] = useState(false);
  const [modal, setModal] = useState<ModalState>({ type: 'none' });

  // Add form
  const [addForm, setAddForm] = useState<AddDrugRequest>(emptyAdd());
  const [addLoading, setAddLoading] = useState(false);

  // Edit form
  const [editForm, setEditForm] = useState<UpdateDrugRequest>({ name: '', brand: '', price: 0 });
  const [editLoading, setEditLoading] = useState(false);

  // Delete
  const [deleteLoading, setDeleteLoading] = useState(false);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      setDrugs(await getAllDrugs());
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to load drugs');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadAll(); }, [loadAll]);

  // ── search ──────────────────────────────────────────────────────────────────

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    const word = searchWord.trim();
    if (!word) { loadAll(); return; }
    setSearching(true);
    try {
      setDrugs(await searchDrugs(word));
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Search failed');
    } finally {
      setSearching(false);
    }
  };

  const clearSearch = () => { setSearchWord(''); loadAll(); };

  // ── add ──────────────────────────────────────────────────────────────────────

  const handleAdd = async (e: FormEvent) => {
    e.preventDefault();
    setAddLoading(true);
    try {
      await addDrug({
        ...addForm,
        expiryDate: addForm.expiryDate || undefined,
      });
      toast.success(`${addForm.name} added`);
      setModal({ type: 'none' });
      setAddForm(emptyAdd());
      loadAll();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to add drug');
    } finally {
      setAddLoading(false);
    }
  };

  // ── edit ─────────────────────────────────────────────────────────────────────

  const openEdit = (drug: Drug) => {
    setEditForm({ name: drug.name, brand: drug.brand, price: drug.unitPrice });
    setModal({ type: 'edit', drug });
  };

  const handleEdit = async (e: FormEvent) => {
    e.preventDefault();
    setEditLoading(true);
    try {
      await updateDrug(editForm);
      toast.success('Drug updated');
      setModal({ type: 'none' });
      loadAll();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Update failed');
    } finally {
      setEditLoading(false);
    }
  };

  // ── delete ───────────────────────────────────────────────────────────────────

  const handleDelete = async () => {
    if (modal.type !== 'delete') return;
    setDeleteLoading(true);
    try {
      await deleteDrug({ name: modal.drug.name });
      toast.success(`${modal.drug.name} deleted`);
      setModal({ type: 'none' });
      loadAll();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Delete failed');
    } finally {
      setDeleteLoading(false);
    }
  };

  // ── render ───────────────────────────────────────────────────────────────────

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Drug Inventory</h1>
          <p className="page-subtitle">{drugs.length} item{drugs.length !== 1 ? 's' : ''}</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal({ type: 'add' })}>
          <Plus size={16} /> Add drug
        </button>
      </div>

      {/* Search bar */}
      <form className="search-bar" onSubmit={handleSearch}>
        <div className="search-input-wrap">
          <Search size={16} className="search-icon" />
          <input
            className="field-input search-input"
            type="text"
            placeholder="Search by name, generic name, or brand…"
            value={searchWord}
            onChange={(e) => setSearchWord(e.target.value)}
          />
          {searchWord && (
            <button type="button" className="btn-icon search-clear" onClick={clearSearch} aria-label="Clear search">
              <X size={14} />
            </button>
          )}
        </div>
        <button className="btn btn-secondary" type="submit" disabled={searching}>
          {searching ? <Loader2 size={15} className="spin" /> : 'Search'}
        </button>
      </form>

      {/* Table */}
      {loading ? (
        <div className="center-spinner"><Loader2 size={32} className="spin" /></div>
      ) : drugs.length === 0 ? (
        <div className="empty-state">
          <Package size={48} strokeWidth={1} />
          <p>No drugs found</p>
        </div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Generic</th>
                <th>Brand</th>
                <th>Strength</th>
                <th>Form</th>
                <th>Price (₦)</th>
                <th>Stock</th>
                <th>Expiry</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {drugs.map((d) => (
                <tr key={d.id} className={d.quantity <= 10 ? 'row-warn' : ''}>
                  <td className="font-medium">{d.name}</td>
                  <td>{d.genericName || '—'}</td>
                  <td>{d.brand}</td>
                  <td>{d.strength || '—'}</td>
                  <td>{d.dosageForm || '—'}</td>
                  <td>₦{d.unitPrice.toLocaleString()}</td>
                  <td>
                    <span className={`badge ${d.quantity === 0 ? 'badge-danger' : d.quantity <= 10 ? 'badge-warn' : 'badge-ok'}`}>
                      {d.quantity}
                    </span>
                  </td>
                  <td>{d.expiryDate ?? '—'}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-icon" onClick={() => openEdit(d)} aria-label="Edit drug">
                        <Pencil size={15} />
                      </button>
                      <button className="btn-icon btn-icon--danger" onClick={() => setModal({ type: 'delete', drug: d })} aria-label="Delete drug">
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* ── Add modal ── */}
      {modal.type === 'add' && (
        <Modal title="Add drug" onClose={() => setModal({ type: 'none' })}>
          <form className="modal-form" onSubmit={handleAdd}>
            <div className="form-grid">
              <label className="field-label">
                Name *
                <input className="field-input" value={addForm.name} onChange={(e) => setAddForm({ ...addForm, name: e.target.value })} required />
              </label>
              <label className="field-label">
                Brand *
                <input className="field-input" value={addForm.brand} onChange={(e) => setAddForm({ ...addForm, brand: e.target.value })} required />
              </label>
              <label className="field-label">
                Generic name
                <input className="field-input" value={addForm.genericName} onChange={(e) => setAddForm({ ...addForm, genericName: e.target.value })} />
              </label>
              <label className="field-label">
                Strength
                <input className="field-input" placeholder="e.g. 500mg" value={addForm.strength} onChange={(e) => setAddForm({ ...addForm, strength: e.target.value })} />
              </label>
              <label className="field-label">
                Dosage form
                <input className="field-input" placeholder="e.g. tablet" value={addForm.dosageForm} onChange={(e) => setAddForm({ ...addForm, dosageForm: e.target.value })} />
              </label>
              <label className="field-label">
                Unit price (₦) *
                <input className="field-input" type="number" min="1" value={addForm.unitPrice || ''} onChange={(e) => setAddForm({ ...addForm, unitPrice: Number(e.target.value) })} required />
              </label>
              <label className="field-label">
                Cost price (₦)
                <input className="field-input" type="number" min="0" value={addForm.costPrice || ''} onChange={(e) => setAddForm({ ...addForm, costPrice: Number(e.target.value) })} />
              </label>
              <label className="field-label">
                Purchase qty
                <input className="field-input" type="number" min="0" value={addForm.purchaseQuantity || ''} onChange={(e) => setAddForm({ ...addForm, purchaseQuantity: Number(e.target.value) })} />
              </label>
              <label className="field-label">
                Expiry date
                <input className="field-input" type="date" value={addForm.expiryDate} onChange={(e) => setAddForm({ ...addForm, expiryDate: e.target.value })} />
              </label>
            </div>
            <div className="modal-actions">
              <button type="button" className="btn btn-ghost" onClick={() => setModal({ type: 'none' })}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={addLoading}>
                {addLoading ? <Loader2 size={16} className="spin" /> : 'Add drug'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* ── Edit modal ── */}
      {modal.type === 'edit' && (
        <Modal title={`Edit — ${modal.drug.name}`} onClose={() => setModal({ type: 'none' })}>
          <form className="modal-form" onSubmit={handleEdit}>
            <div className="form-grid">
              <label className="field-label">
                Brand
                <input className="field-input" value={editForm.brand} onChange={(e) => setEditForm({ ...editForm, brand: e.target.value })} />
              </label>
              <label className="field-label">
                Unit price (₦)
                <input className="field-input" type="number" min="1" value={editForm.price || ''} onChange={(e) => setEditForm({ ...editForm, price: Number(e.target.value) })} />
              </label>
            </div>
            <div className="modal-actions">
              <button type="button" className="btn btn-ghost" onClick={() => setModal({ type: 'none' })}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={editLoading}>
                {editLoading ? <Loader2 size={16} className="spin" /> : 'Save changes'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* ── Delete confirm ── */}
      {modal.type === 'delete' && (
        <Modal title="Delete drug" onClose={() => setModal({ type: 'none' })}>
          <p className="confirm-text">
            Are you sure you want to delete <strong>{modal.drug.name}</strong>? This will also remove all its batches.
          </p>
          <div className="modal-actions">
            <button className="btn btn-ghost" onClick={() => setModal({ type: 'none' })}>Cancel</button>
            <button className="btn btn-danger" onClick={handleDelete} disabled={deleteLoading}>
              {deleteLoading ? <Loader2 size={16} className="spin" /> : 'Delete'}
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}
