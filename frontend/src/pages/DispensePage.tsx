import { useState, useEffect, useCallback, type FormEvent } from 'react';
import { Plus, Trash2, Loader2, ShoppingCart, Search, X } from 'lucide-react';
import toast from 'react-hot-toast';
import { getAllDrugs, searchDrugs, dispenseDrugs } from '../api/drugs';
import { useAuth } from '../context/AuthContext';
import type { Drug, DispensedDrugItem } from '../types';

interface CartItem extends DispensedDrugItem {
  unitPrice: number;
}

export default function DispensePage() {
  const { user } = useAuth();

  const [drugs, setDrugs] = useState<Drug[]>([]);
  const [drugsLoading, setDrugsLoading] = useState(true);
  const [searchWord, setSearchWord] = useState('');
  const [searching, setSearching] = useState(false);

  const [cart, setCart] = useState<CartItem[]>([]);
  const [submitting, setSubmitting] = useState(false);

  // Selected drug for adding to cart
  const [selected, setSelected] = useState<Drug | null>(null);
  const [selBatchId, setSelBatchId] = useState('');
  const [selQty, setSelQty] = useState(1);

  const loadDrugs = useCallback(async () => {
    setDrugsLoading(true);
    try {
      setDrugs(await getAllDrugs());
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to load drugs');
    } finally {
      setDrugsLoading(false);
    }
  }, []);

  useEffect(() => { loadDrugs(); }, [loadDrugs]);

  // ── search ──────────────────────────────────────────────────────────────────

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    const word = searchWord.trim();
    if (!word) { loadDrugs(); return; }
    setSearching(true);
    try {
      setDrugs(await searchDrugs(word));
      setSelected(null);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Search failed');
    } finally {
      setSearching(false);
    }
  };

  const clearSearch = () => { setSearchWord(''); loadDrugs(); };

  // ── select drug from list ────────────────────────────────────────────────────

  const selectDrug = (drug: Drug) => {
    setSelected(drug);
    const firstBatch = drug.batches?.find((b) => b.quantityLeft > 0);
    setSelBatchId(firstBatch?.id ?? '');
    setSelQty(1);
  };

  // ── add to cart ──────────────────────────────────────────────────────────────

  const addToCart = () => {
    if (!selected) return;
    if (!selBatchId) { toast.error('Select a batch'); return; }
    if (selQty < 1) { toast.error('Quantity must be at least 1'); return; }

    const batch = selected.batches?.find((b) => b.id === selBatchId);
    if (!batch) { toast.error('Batch not found'); return; }
    if (selQty > batch.quantityLeft) {
      toast.error(`Only ${batch.quantityLeft} left in this batch`);
      return;
    }

    const existingIdx = cart.findIndex(
      (c) => c.drugName === selected.name && c.batchId === selBatchId
    );

    if (existingIdx >= 0) {
      const updated = [...cart];
      const item = updated[existingIdx];
      const newQty = item.quantity + selQty;
      if (newQty > batch.quantityLeft) {
        toast.error(`Total would exceed available stock (${batch.quantityLeft})`);
        return;
      }
      updated[existingIdx] = {
        ...item,
        quantity: newQty,
        totalPrice: newQty * item.unitPrice,
      };
      setCart(updated);
    } else {
      setCart([
        ...cart,
        {
          batchId: selBatchId,
          drugId: selected.id,
          drugName: selected.name,
          quantity: selQty,
          totalPrice: selQty * selected.unitPrice,
          unitPrice: selected.unitPrice,
        },
      ]);
    }
    toast.success(`${selected.name} added to cart`);
    setSelected(null);
  };

  const removeFromCart = (idx: number) => {
    setCart(cart.filter((_, i) => i !== idx));
  };

  const totalAmount = cart.reduce((sum, c) => sum + c.totalPrice, 0);

  // ── dispense ─────────────────────────────────────────────────────────────────

  const handleDispense = async () => {
    if (!user) { toast.error('Not logged in'); return; }
    if (cart.length === 0) { toast.error('Cart is empty'); return; }
    setSubmitting(true);
    try {
      const res = await dispenseDrugs({ username: user.username, items: cart });
      toast.success(res.message);
      setCart([]);
      loadDrugs();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Dispense failed');
    } finally {
      setSubmitting(false);
    }
  };

  // ── render ───────────────────────────────────────────────────────────────────

  const availableBatches = selected?.batches?.filter((b) => b.quantityLeft > 0) ?? [];

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Dispense Drugs</h1>
          <p className="page-subtitle">Select drugs to add to the dispensing cart</p>
        </div>
      </div>

      <div className="dispense-layout">
        {/* ── Left: drug picker ── */}
        <div className="dispense-picker card">
          <h2 className="card-title">Drug list</h2>

          <form className="search-bar" onSubmit={handleSearch}>
            <div className="search-input-wrap">
              <Search size={16} className="search-icon" />
              <input
                className="field-input search-input"
                type="text"
                placeholder="Search drugs…"
                value={searchWord}
                onChange={(e) => setSearchWord(e.target.value)}
              />
              {searchWord && (
                <button type="button" className="btn-icon search-clear" onClick={clearSearch} aria-label="Clear">
                  <X size={14} />
                </button>
              )}
            </div>
            <button className="btn btn-secondary" type="submit" disabled={searching}>
              {searching ? <Loader2 size={15} className="spin" /> : 'Search'}
            </button>
          </form>

          {drugsLoading ? (
            <div className="center-spinner"><Loader2 size={24} className="spin" /></div>
          ) : (
            <ul className="drug-pick-list">
              {drugs.map((d) => (
                <li
                  key={d.id}
                  className={`drug-pick-item${selected?.id === d.id ? ' drug-pick-item--active' : ''}${d.quantity === 0 ? ' drug-pick-item--oos' : ''}`}
                  onClick={() => d.quantity > 0 && selectDrug(d)}
                  aria-disabled={d.quantity === 0}
                >
                  <div className="drug-pick-name">{d.name} <span className="drug-pick-brand">({d.brand})</span></div>
                  <div className="drug-pick-meta">
                    ₦{d.unitPrice.toLocaleString()} &bull;{' '}
                    <span className={d.quantity === 0 ? 'text-danger' : ''}>{d.quantity} in stock</span>
                  </div>
                </li>
              ))}
            </ul>
          )}

          {/* Batch / qty selector */}
          {selected && (
            <div className="batch-selector card card--inset">
              <p className="batch-drug-name">{selected.name}</p>
              {availableBatches.length === 0 ? (
                <p className="text-danger">No stock available</p>
              ) : (
                <>
                  <label className="field-label">
                    Batch
                    <select
                      className="field-input"
                      value={selBatchId}
                      onChange={(e) => setSelBatchId(e.target.value)}
                    >
                      {availableBatches.map((b) => (
                        <option key={b.id} value={b.id}>
                          Batch {b.id.slice(-6)} — {b.quantityLeft} left (exp: {b.purchaseDate})
                        </option>
                      ))}
                    </select>
                  </label>
                  <label className="field-label">
                    Quantity
                    <input
                      className="field-input"
                      type="number"
                      min="1"
                      max={availableBatches.find((b) => b.id === selBatchId)?.quantityLeft}
                      value={selQty}
                      onChange={(e) => setSelQty(Number(e.target.value))}
                    />
                  </label>
                  <button className="btn btn-primary" onClick={addToCart}>
                    <Plus size={15} /> Add to cart
                  </button>
                </>
              )}
            </div>
          )}
        </div>

        {/* ── Right: cart ── */}
        <div className="dispense-cart card">
          <h2 className="card-title">
            <ShoppingCart size={18} /> Cart ({cart.length})
          </h2>

          {cart.length === 0 ? (
            <p className="cart-empty">No items yet. Select drugs from the list.</p>
          ) : (
            <>
              <ul className="cart-list">
                {cart.map((item, idx) => (
                  <li key={idx} className="cart-item">
                    <div className="cart-item-info">
                      <span className="cart-item-name">{item.drugName}</span>
                      <span className="cart-item-meta">
                        {item.quantity} × ₦{item.unitPrice.toLocaleString()} = ₦{item.totalPrice.toLocaleString()}
                      </span>
                    </div>
                    <button className="btn-icon btn-icon--danger" onClick={() => removeFromCart(idx)} aria-label="Remove">
                      <Trash2 size={15} />
                    </button>
                  </li>
                ))}
              </ul>

              <div className="cart-total">
                <span>Total</span>
                <span className="cart-total-amount">₦{totalAmount.toLocaleString()}</span>
              </div>

              <button
                className="btn btn-primary btn-full"
                onClick={handleDispense}
                disabled={submitting}
              >
                {submitting ? <Loader2 size={16} className="spin" /> : 'Confirm dispense'}
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
